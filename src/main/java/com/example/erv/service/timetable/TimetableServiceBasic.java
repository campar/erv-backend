package com.example.erv.service.timetable;

import java.time.ZonedDateTime;
import java.util.List;

import com.example.erv.dto.projection.TimetableMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.timetable.TimetableRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class TimetableServiceBasic extends GenericService<Timetable> {
	protected final TimetableRepository repository;

	public TimetableServiceBasic(TimetableRepository repository) {
		super(repository);
		this.repository = repository;
	}
    
    @Override
	public List<TimetableMinimal> findAllMinimal(){
		return repository.findAllProjected(TimetableMinimal.class);
	}
	
	@Override
	public Timetable insert(Timetable object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Timetable update(Timetable object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Timetable> getEntityClass() {
		return Timetable.class;
	}

	public List<Timetable> findByActionType(ActionType actionType) {
		return repository.findByActionType(actionType);
	}
	public List<Timetable> findByDateTime(ZonedDateTime dateTime) {
		return repository.findByDateTime(dateTime.toInstant());
	}

	public List<Timetable> findByGroupingKey(Long groupingKey) {
		return repository.findByGroupingKey(groupingKey);
	}
}
