package com.example.erv.repository.departmentunit;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentUnitRepository extends DepartmentUnitRepositoryBasic {
	<T> List<T> findBySoftDeletedFalse(Class<T> type);
}
