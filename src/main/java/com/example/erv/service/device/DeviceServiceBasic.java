package com.example.erv.service.device;

import java.util.List;

import com.example.erv.dto.projection.DeviceMinimal;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Device;
import com.example.erv.repository.device.DeviceRepository;
import com.example.erv.service.GenericService;

import jakarta.persistence.EntityNotFoundException;

public class DeviceServiceBasic extends GenericService<Device> {
	protected final DeviceRepository repository;

	public DeviceServiceBasic(DeviceRepository repository) {
		super(repository);
		this.repository = repository;
	}
	
	@Override
	public List<DeviceMinimal> findAllMinimal(){
		return repository.findAllProjected(DeviceMinimal.class);
	}

	@Override
	public Device insert(Device object) throws ErvException {
		if(object.getId() != null) throw new ErvException("Entity already exists: " + object.getId());
		return repository.save(object);
	}

	@Override
	public Device update(Device object) throws ErvException {
		if(object.getId() == null || repository.findById(object.getId()).isEmpty()) throw new EntityNotFoundException(String.valueOf(object.getId()));
		return repository.save(object);
	}

	@Override
	public Class<Device> getEntityClass() {
		return Device.class;
	}

	public List<Device> findAllByCode(String code) {
		return repository.findByCode(code);
	}
	
	public Device findByCode(String code) {
	    return repository.findByCode(code).stream()
	            .filter(device -> Boolean.FALSE.equals(device.getSoftDeleted()))
	            .findFirst()
	            .orElse(null);
	}
	
	public Device findByHardwareIdentifier(String hardwareIdentifier) {
		return repository.findByHardwareIdentifier(hardwareIdentifier).orElseThrow();
	}
	
	public List<Device> findByActive(Boolean active) {
		return repository.findByActive(active);
	}

	public List<Device> findByDescription(String description) {
		return repository.findByDescription(description);
	}
}
