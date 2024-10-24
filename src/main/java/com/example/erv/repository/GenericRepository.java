package com.example.erv.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
	Page<T> findAll(Specification<T> spec, Pageable pageSort);
	List<T> findAll(Specification<T> spec);
	@Query("SELECT e FROM #{#entityName} e")
	<P> List<P> findAllProjected(Class<P> projection);
}