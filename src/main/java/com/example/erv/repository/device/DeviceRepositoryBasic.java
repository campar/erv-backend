package com.example.erv.repository.device;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Device;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface DeviceRepositoryBasic extends GenericRepository<Device> {

	List<Device> findByCode(String code);
	Optional<Device> findByHardwareIdentifier(String hardwareIdentifier);
	List<Device> findByActive(Boolean active);
	List<Device> findByDescription(String description);
	
	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.softDeleted = false")
	<P> List<P> findAllProjected(Class<P> projection);

}
