package com.example.erv.repository.shift;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.erv.model.Shift;

import jakarta.persistence.LockModeType;

@Repository
public interface ShiftRepository extends ShiftRepositoryBasic {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Shift s WHERE s.id = :id")
	Optional<Shift> findByIdWithLock(@Param("id") Long id);
}
