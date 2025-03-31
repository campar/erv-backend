package com.example.erv.repository.timetable;

import java.time.Instant;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import com.example.erv.model.Shift;
import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.GenericRepository;

@NoRepositoryBean
public interface TimetableRepositoryBasic extends GenericRepository<Timetable> {

    List<Timetable> findByActionType(ActionType actionType);

    List<Timetable> findByDateTime(Instant dateTime);

    long countByShift(Shift shift);

    List<Timetable> findByGroupingKey(Long groupingKey);
}
