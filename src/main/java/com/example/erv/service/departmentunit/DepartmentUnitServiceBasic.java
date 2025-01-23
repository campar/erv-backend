package com.example.erv.service.departmentunit;

import java.util.List;

import com.example.erv.dto.projection.DepartmentUnitMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.repository.departmentunit.DepartmentUnitRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class DepartmentUnitServiceBasic extends GenericService<DepartmentUnit> {
	protected final DepartmentUnitRepository repository;

	public DepartmentUnitServiceBasic(DepartmentUnitRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<DepartmentUnitMinimal> findAllMinimal(){
		return repository.findAllProjected(DepartmentUnitMinimal.class);
	}

	@Override
	public DepartmentUnit insert(DepartmentUnit object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public DepartmentUnit update(DepartmentUnit object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<DepartmentUnit> getEntityClass() {
		return DepartmentUnit.class;
	}

	public List<DepartmentUnit> findByName(String name) {
		return repository.findByName(name);
	}
	public List<DepartmentUnit> findAllByCode(String code) {
		return repository.findByCode(code);
	}
	public DepartmentUnit findByCode(String code) {
		return repository.findByCode(code).stream()
	            .filter(departmentUnit -> Boolean.FALSE.equals(departmentUnit.getSoftDeleted()))
	            .findFirst()
	            .orElse(null);
	}
	public List<DepartmentUnit> findByDescription(String description) {
		return repository.findByDescription(description);
	}
}
