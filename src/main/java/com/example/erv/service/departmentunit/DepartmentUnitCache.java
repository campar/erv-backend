package com.example.erv.service.departmentunit;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.repository.departmentunit.DepartmentUnitRepository;

@Service
public class DepartmentUnitCache {
    @Autowired
    private DepartmentUnitRepository departmentUnitRepository;

    @Cacheable("departmentUnitCache")
    public List<DepartmentUnit> findAll() {
        return departmentUnitRepository.findBySoftDeletedFalse(DepartmentUnit.class);
    }

    @CacheEvict(value = "departmentUnitCache", allEntries = true)
    public DepartmentUnit save(DepartmentUnit object) {
        return departmentUnitRepository.save(object);
    }

    @CacheEvict(value = "departmentUnitCache", allEntries = true)
    public void deleteById(Long id) {
        departmentUnitRepository.deleteById(id);
    }
    
    @CacheEvict(value = "departmentUnitCache", allEntries = true)
    public void softDelete(Long id) {
        Optional<DepartmentUnit> optional = departmentUnitRepository.findById(id);
        if (optional.isPresent()) {
            DepartmentUnit departmentUnit = optional.get();
            departmentUnit.setSoftDeleted(true);
            departmentUnitRepository.save(departmentUnit);
        }
    }

}
