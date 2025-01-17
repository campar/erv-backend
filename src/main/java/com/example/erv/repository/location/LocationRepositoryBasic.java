package com.example.erv.repository.location;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Location;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface LocationRepositoryBasic extends GenericRepository<Location> {

	List<Location> findByName(String name);
	List<Location> findByAddress(String address);
	List<Location> findByCity(String city);
	List<Location> findByPhone(String phone);
	List<Location> findByDescription(String description);

	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.softDeleted = false")
	<P> List<P> findAllProjected(Class<P> projection);
}
