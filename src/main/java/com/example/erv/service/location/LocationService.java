package com.example.erv.service.location;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.erv.exceptions.DeviceContainsLocationException;
import com.example.erv.exceptions.ErvException;
import com.example.erv.model.Location;
import com.example.erv.repository.location.LocationRepository;
import com.example.erv.specification.SpecificationBasic;

@Service
public class LocationService extends LocationServiceBasic {
	public LocationService(LocationRepository repository) {
		super(repository);
	}

	@Override
	public void deleteById(Long id) throws ErvException {
		Location location = repository.findById(id).get();

		if (!location.getDeviceList().isEmpty()) {
			throw new DeviceContainsLocationException("Location is connected to devices. Set new location to devices then delete this location.");
		}

		location.setSoftDeleted(true);
		if (location.getDeviceList().isEmpty()) {
			super.deleteById(id);
			return;
		}

		repository.save(location);
	}

	@Override
	public List<Location> findAll() {
		SpecificationBasic<Location> spec = SpecificationBasic.where(LocationAttribute.SOFT_DELETED, false);
		return super.findAll(spec.getSpecification());
	}

	@Override
	public List<Location> findAll(Specification<Location> specification) {
		SpecificationBasic<Location> spec = SpecificationBasic.where(LocationAttribute.SOFT_DELETED, false);
		return super.findAll(spec.and(specification).getSpecification());
	}

	@Override
	public Page<Location> findAll(Pageable pageable) {
		SpecificationBasic<Location> spec = SpecificationBasic.where(LocationAttribute.SOFT_DELETED, false);
		return super.findAll(spec.getSpecification(), pageable);
	}

	@Override
	public Page<Location> findAll(Specification<Location> specification, Pageable pageable) {
		SpecificationBasic<Location> spec = SpecificationBasic.where(LocationAttribute.SOFT_DELETED, false);
		return super.findAll(spec.and(specification).getSpecification(), pageable);
	}
}
