package com.example.erv.repository.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Role;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface RoleRepositoryBasic extends GenericRepository<Role> {

	Optional<Role> findByName(String name);
	List<Role> findByDescription(String description);

}
