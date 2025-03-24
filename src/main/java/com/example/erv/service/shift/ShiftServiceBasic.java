package com.example.erv.service.shift;

import java.util.List;

import com.example.erv.dto.projection.ShiftMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Shift;
import com.example.erv.repository.shift.ShiftRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class ShiftServiceBasic extends GenericService<Shift> {
	protected final ShiftRepository repository;

	public ShiftServiceBasic(ShiftRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<ShiftMinimal> findAllMinimal(){
		return repository.findAllProjected(ShiftMinimal.class);
	}

	@Override
	public Shift insert(Shift object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Shift update(Shift object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Shift> getEntityClass() {
		return Shift.class;
	}

	public List<Shift> findByStartFrom(String startFrom) {
		return repository.findByStartFrom(startFrom);
	}
	public List<Shift> findByStartTo(String startTo) {
		return repository.findByStartTo(startTo);
	}
	public List<Shift> findByEndFrom(String endFrom) {
		return repository.findByEndFrom(endFrom);
	}
	public List<Shift> findByEndTo(String endTo) {
		return repository.findByEndTo(endTo);
	}
	public List<Shift> findByDurationHours(Integer durationHours) {
		return repository.findByDurationHours(durationHours);
	}
	public List<Shift> findByDurationMinutes(Integer durationMinutes) {
		return repository.findByDurationMinutes(durationMinutes);
	}
}
