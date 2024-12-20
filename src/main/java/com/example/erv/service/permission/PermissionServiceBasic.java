package com.example.erv.service.permission;

import java.util.List;

import com.example.erv.dto.projection.PermissionMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Permission;
import com.example.erv.repository.permission.PermissionRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class PermissionServiceBasic extends GenericService<Permission> {
	protected final PermissionRepository repository;

	public PermissionServiceBasic(PermissionRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<PermissionMinimal> findAllMinimal(){
		return repository.findAllProjected(PermissionMinimal.class);
	}

	@Override
	public Permission insert(Permission object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Permission update(Permission object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Permission> getEntityClass() {
		return Permission.class;
	}

	public Permission findByName(String name) {
		return repository.findByName(name).orElseThrow();
	}
	public List<Permission> findByDescription(String description) {
		return repository.findByDescription(description);
	}
}
