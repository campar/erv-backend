package com.example.erv.service.role;

import java.util.List;

import com.example.erv.dto.projection.RoleMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Role;
import com.example.erv.repository.role.RoleRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class RoleServiceBasic extends GenericService<Role> {
	protected final RoleRepository repository;

	public RoleServiceBasic(RoleRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<RoleMinimal> findAllMinimal(){
		return repository.findAllProjected(RoleMinimal.class);
	}
	
	@Override
	public Role insert(Role object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Role update(Role object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Role> getEntityClass() {
		return Role.class;
	}

	public Role findByName(String name) {
		return repository.findByName(name).orElseThrow();
	}
	public List<Role> findByDescription(String description) {
		return repository.findByDescription(description);
	}
}
