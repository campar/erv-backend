package com.example.erv.service.departmentunit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.erv.exceptions.CircularParentReferenceException;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.repository.departmentunit.DepartmentUnitRepository;
import com.example.erv.repository.user.UserRepository;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.SecurityUtils;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DepartmentUnitService extends DepartmentUnitServiceBasic {
    @Autowired
    private DepartmentUnitCache cache;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private UserRepository userRepository;

    public DepartmentUnitService(DepartmentUnitRepository repository) {
        super(repository);
    }

    public List<DepartmentUnit> getAllAccessible(User user) {
    	List<DepartmentUnit> allUnits = cache.findAll();
        
        if (SecurityUtils.isSuperUser(user)) {
            return allUnits;
        }
        if (user.getDepartmentUnit() == null && !CollectionUtils.isEmpty(user.getAdminAccessibleDepartmentUnits())) return List.of();
        
        Set<DepartmentUnit> accessibleUnits = new HashSet<>(getAllSubUnits(user.getDepartmentUnit(), allUnits));
        
        if(!CollectionUtils.isEmpty(user.getAdminAccessibleDepartmentUnits())){
        	accessibleUnits.addAll(user.getAdminAccessibleDepartmentUnits().stream().filter(unit -> !unit.getSoftDeleted()).toList());
        }
        return new ArrayList<DepartmentUnit>(accessibleUnits);
    }

    private List<DepartmentUnit> getAllSubUnits(DepartmentUnit parent, List<DepartmentUnit> allUnits) {
        List<DepartmentUnit> units = new ArrayList<>();
        if(!parent.getSoftDeleted()) {
        	units.add(parent);
        }
        
        List<DepartmentUnit> subUnits = allUnits.stream()
                .filter(unit -> unit.getParent() != null && parent.getId().equals(unit.getParent().getId()) && !parent.getSoftDeleted())
                .toList();
        
        for (DepartmentUnit subUnit : subUnits) {
        	if(subUnit.getSoftDeleted()) continue;
            units.addAll(getAllSubUnits(subUnit, allUnits));
        }
        return units;
    }

    @Override
    public Page<DepartmentUnit> findAll(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
        	SpecificationBasic<DepartmentUnit> specAll = SpecificationBasic.<DepartmentUnit>where(DepartmentUnitAttribute.SOFT_DELETED, false);
            return super.findAll(specAll.getSpecification(), pageable);
        }
        List<DepartmentUnit> accessible = getAllAccessible(user);
        return createPageFromMemory(pageable, accessible);
    }

    @Override
    public Page<DepartmentUnit> findAll(Specification<DepartmentUnit> specification, Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        if (SecurityUtils.isSuperUser(user)) {
        	SpecificationBasic<DepartmentUnit> specAll = SpecificationBasic.<DepartmentUnit>where(DepartmentUnitAttribute.SOFT_DELETED, false).and(specification);
            return super.findAll(specAll.getSpecification(), pageable);
        }
        List<DepartmentUnit> accessible = getAllAccessible(user);
        SpecificationBasic<DepartmentUnit> spec = SpecificationBasic.<DepartmentUnit>where(DepartmentUnitAttribute.ID, SearchOperator.IN, accessible.stream().map(DepartmentUnit::getId).toList())
                .and(specification).and(DepartmentUnitAttribute.SOFT_DELETED, false);
        return super.findAll(spec.getSpecification(), pageable);
    }

    private PageImpl<DepartmentUnit> createPageFromMemory(Pageable pageable, List<DepartmentUnit> accessible) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<DepartmentUnit> subList;

        if (accessible.size() < startItem) {
            subList = List.of(); // Return empty list if starting item is out of list range
        } else {
            int toIndex = Math.min(startItem + pageSize, accessible.size());
            subList = accessible.subList(startItem, toIndex);
        }
        return new PageImpl<>(subList, pageable, accessible.size());
    }

    @Override
    public List<DepartmentUnit> findAll() {
        return getAllAccessible(securityUtils.getCurrentUser());
    }

    @Override
    public DepartmentUnit save(DepartmentUnit object) {
        return cache.save(object);
    }

    @Override
    public DepartmentUnit insert(DepartmentUnit object) throws ErvException {
        if (object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
        validateNoCircularParenting(object);
        if(object.getCode() != null && findByCode(object.getCode()) != null) {
			ConstraintViolationException cause = new ConstraintViolationException("Duplicate key value violates unique constraint", null ,ConstraintKind.UNIQUE, "department_code_key");
			throw new DataIntegrityViolationException("Duplicate key value violates unique constraint", cause);
		}
        return cache.save(object);
    }

    @Override
    public DepartmentUnit update(DepartmentUnit object) throws ErvException {
        if (object.getId() == null || repository.findById(object.getId()).isEmpty())
            throw new EntityNotFoundException(String.valueOf(object.getId()));
        if (object.getParent() != null && object.getId().equals(object.getParent().getId()))
            throw new InvalidDepartmentUnitParentException(object);
        validateNoCircularParenting(object);
		if(object.getCode() != null && findByCode(object.getCode())!= null && !object.getId().equals(findByCode(object.getCode()).getId())) {
			ConstraintViolationException cause = new ConstraintViolationException("Duplicate key value violates unique constraint", null ,ConstraintKind.UNIQUE, "department_code_key");
			throw new DataIntegrityViolationException("Duplicate key value violates unique constraint", cause);
		}
        return cache.save(object);
    }

    @Override
    public void deleteById(Long id) throws ErvException {
        Optional<DepartmentUnit> optional = repository.findById(id);
        if (optional.isEmpty()) throw new ErvException("Object not found: " + id);
        Long users = userRepository.countByDepartmentUnit(optional.get());
        if (users > 0 && containsActiveUser(optional.get().getUserList())) {
            throw new DepartmentUnitDeleteException(id);
        }else if(users > 0) {
        	cache.softDelete(id);
        }else {
        	cache.deleteById(id);
        }
    }
    
    private boolean containsActiveUser(List<User> users) {
    	for(User user: users) {
    		if(!user.getSoftDeleted()) return true;
    	}
    	return false;
    }
    
    private void validateNoCircularParenting(DepartmentUnit candidate) throws CircularParentReferenceException {
        DepartmentUnit current = candidate.getParent();
        while (current != null) {
            if (current.getId().equals(candidate.getId())) {
                throw new CircularParentReferenceException("Circular parent reference detected: DepartmentUnit cannot be its own ancestor.");
            }
            current = current.getParent();
        }
    }

}
