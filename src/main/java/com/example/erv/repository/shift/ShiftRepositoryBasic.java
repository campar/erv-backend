package com.example.erv.repository.shift;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Shift;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface ShiftRepositoryBasic extends GenericRepository<Shift> {

	List<Shift> findByStartFrom(String startFrom);
	List<Shift> findByStartTo(String startTo);
	List<Shift> findByEndFrom(String endFrom);
	List<Shift> findByEndTo(String endTo);
	List<Shift> findByDurationHours(Integer durationHours);
	List<Shift> findByDurationMinutes(Integer durationMinutes);

	@Override
	@Query("SELECT e FROM #{#entityName} e WHERE e.softDeleted = false")
	<P> List<P> findAllProjected(Class<P> projection);
}
