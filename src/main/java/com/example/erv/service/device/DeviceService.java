package com.example.erv.service.device;

import java.time.Instant;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Device;
import com.example.erv.repository.device.DeviceRepository;
import com.example.erv.specification.SpecificationBasic;

@Service
public class DeviceService extends DeviceServiceBasic {

	public DeviceService(DeviceRepository repository) {
		super(repository);
	}
	
	@Override
	public Device insert(Device object) throws ErvException {
		if(findByCode(object.getCode()) != null) {
			ConstraintViolationException cause = new ConstraintViolationException("Duplicate key value violates unique constraint", null ,ConstraintKind.UNIQUE, "device_code_key");
			throw new DataIntegrityViolationException("Duplicate key value violates unique constraint", cause);
		}
		return super.insert(object);
	}
	
	@Override
	public Device update(Device object) throws ErvException {
		if(findByCode(object.getCode())!= null && !object.getId().equals(findByCode(object.getCode()).getId())) {
			ConstraintViolationException cause = new ConstraintViolationException("Duplicate key value violates unique constraint", null ,ConstraintKind.UNIQUE, "device_code_key");
			throw new DataIntegrityViolationException("Duplicate key value violates unique constraint", cause);
		}
		return super.update(object);
	}

	@Override
	public void deleteById(Long id) throws ErvException {
		Device device = repository.findById(id).get();

		if (device.getTimetableList().isEmpty()) {
			super.deleteById(id);
			return;
		}
		device.setHardwareIdentifier(null);
		device.setSoftDeleted(true);
		device.setActive(false);

		repository.save(device);
	}
	
	@Override
	public List<Device> findAll() {
		 SpecificationBasic<Device> spec = SpecificationBasic.where(DeviceAttribute.SOFT_DELETED, false);
		return super.findAll(spec.getSpecification());
	}
	@Override
	public List<Device> findAll(Specification<Device> specification) {
		 SpecificationBasic<Device> spec = SpecificationBasic.where(DeviceAttribute.SOFT_DELETED, false);
		return super.findAll(spec.and(specification).getSpecification());
	}
	@Override
	public Page<Device> findAll(Pageable pageable) {
		 SpecificationBasic<Device> spec = SpecificationBasic.where(DeviceAttribute.SOFT_DELETED, false);
		return super.findAll(spec.getSpecification(), pageable);
	}
	@Override
	public Page<Device> findAll(Specification<Device> specification, Pageable pageable) {
		 SpecificationBasic<Device> spec = SpecificationBasic.where(DeviceAttribute.SOFT_DELETED, false);
		return super.findAll(spec.and(specification).getSpecification(), pageable);
	}

	public void recordHeartbeat(String hardwareId, String ipAddress) {
		Device device = repository.findByHardwareIdentifier(hardwareId).orElseThrow();
		device.setLastKnownIpAddress(ipAddress);
		device.setLastOnlineAt(Instant.now());
		repository.save(device);
	}
}
