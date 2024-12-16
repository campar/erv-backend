package com.example.erv.repository.permission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Permission;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface PermissionRepositoryBasic extends GenericRepository<Permission> {

	Optional<Permission> findByName(String name);
	List<Permission> findByDescription(String description);

}
