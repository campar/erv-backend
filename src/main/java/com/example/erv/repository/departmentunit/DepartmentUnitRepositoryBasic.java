package com.example.erv.repository.departmentunit;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface DepartmentUnitRepositoryBasic extends GenericRepository<DepartmentUnit> {

	List<DepartmentUnit> findByName(String name);
	List<DepartmentUnit> findByCode(String code);
	List<DepartmentUnit> findByDescription(String description);
	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.softDeleted = false")
	<P> List<P> findAllProjected(Class<P> projection);
}
