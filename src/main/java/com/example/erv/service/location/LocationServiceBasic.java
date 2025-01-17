package com.example.erv.service.location;

import java.util.List;

import com.example.erv.dto.projection.LocationMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Location;
import com.example.erv.repository.location.LocationRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class LocationServiceBasic extends GenericService<Location> {
	protected final LocationRepository repository;

	public LocationServiceBasic(LocationRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<LocationMinimal> findAllMinimal() {
		return repository.findAllProjected(LocationMinimal.class);
	}
	
	@Override
	public Location insert(Location object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Location update(Location object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Location> getEntityClass() {
		return Location.class;
	}

	public List<Location> findByName(String name) {
		return repository.findByName(name);
	}
	public List<Location> findByAddress(String address) {
		return repository.findByAddress(address);
	}
	public List<Location> findByCity(String city) {
		return repository.findByCity(city);
	}
	public List<Location> findByPhone(String phone) {
		return repository.findByPhone(phone);
	}
	public List<Location> findByDescription(String description) {
		return repository.findByDescription(description);
	}
}
