package com.example.erv.repository.timetable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.erv.dto.ActionCount;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;

@Repository
public interface TimetableRepository extends TimetableRepositoryBasic {

    @Query(value = "select new com.example.erv.repository.timetable.CheckInStatsDTO(t.device, t.actionType, count(t.actionType)) " +
            "from Timetable t " +
            "where t.dateTime between :startDate AND :endDate " +
            "and t.device.id in (:devices) " +
            "group by t.device, t.actionType ")
    List<CheckInStatsDTO> findCheckInStats(List<Long> devices, Instant startDate, Instant endDate);


    @Query(value = "select t.date_time::date AS date from timetable t " +
            "where t.erv_user = :userId " +
            "and t.date_time >= :startDate " +
            "and t.date_time <= :endDate " +
            "and t.action_type in (:actionType) " +
            "group by date " +
            "order by date desc",
            countQuery = "SELECT COUNT(*) FROM " +
                    "(SELECT t.date_time::date AS date from timetable t WHERE t.erv_user = :userId and t.date_time >= :startDate and t.date_time <= :endDate and t.action_type in (:actionType) group by date) as date_groups",
            nativeQuery = true)
    Page<LocalDate> findDatesByRangeAndUser(Instant startDate, Instant endDate, Long userId, List<String> actionType, Pageable pageable);

    @Query(value = "select t.* from timetable t " +
            "where t.erv_user = :userId " +
            "and t.grouping_key IN (" +
            "   SELECT x.grouping_key " +
            "   FROM timetable x " +
            "   WHERE x.action_type = 'CHECK_IN' " +
            "   AND x.date_time::date in (:dates) " +
            "   AND x.erv_user = :userId) " +
            "and t.action_type in (:actionType) " +
            "order by t.date_time desc",
            countQuery = "select count(*) from timetable t " +
                    "where t.erv_user = :userId " +
                    "and t.grouping_key IN (" +
                    "   SELECT x.grouping_key " +
                    "   FROM timetable x " +
                    "   WHERE x.action_type = 'CHECK_IN' " +
                    "   AND x.date_time::date in (:dates) " +
                    "   AND x.erv_user = :userId) " +
                    "and t.action_type in (:actionType) " +
                    "order by t.date_time desc",
            nativeQuery = true)
    List<Timetable> findByDates(List<LocalDate> dates, Long userId, List<String> actionType);
    
    /**
     * There is exactly one CHECK_IN action.<br>
     * There is no CHECK_OUT action.<br>
     * There is exactly one BUSINESS_IN action.<br>
     * There is no BUSINESS_OUT action.
     */
    @Query(value = "SELECT erv_user, shift, date_time::date " +
            "FROM timetable " +
            "WHERE action_type IN ('CHECK_IN', 'CHECK_OUT', 'BUSINESS_IN', 'BUSINESS_OUT') " +
            "AND date_time::date < CURRENT_DATE - INTERVAL '1 day' " +
            "GROUP BY erv_user, shift, date_time::date " +
            "HAVING SUM(CASE WHEN action_type = 'CHECK_IN' THEN 1 ELSE 0 END) = 1 " +
            "AND SUM(CASE WHEN action_type = 'CHECK_OUT' THEN 1 ELSE 0 END) = 0 " +
            "AND SUM(CASE WHEN action_type = 'BUSINESS_IN' THEN 1 ELSE 0 END) = 1 " +
            "AND SUM(CASE WHEN action_type = 'BUSINESS_OUT' THEN 1 ELSE 0 END) = 0;",
            nativeQuery = true)
    List<Object[]> findDataForCheckOutJob();

    @Query("SELECT new com.example.erv.dto.ActionCount(" +
            "SUM(CASE WHEN t.actionType = 'PAUSE_START' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.actionType = 'PAUSE_END' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.actionType = 'BUSINESS_START' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.actionType = 'BUSINESS_END' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.actionType = 'CHECK_IN' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN t.actionType = 'CHECK_OUT' THEN 1 ELSE 0 END)) " +
            "FROM Timetable t JOIN t.user u WHERE u.departmentUnit IN (:departmentUnits) AND t.dateTime BETWEEN :startDate AND :endDate")
    ActionCount countActionTypesBetweenDates(List<DepartmentUnit> departmentUnits, Instant startDate, Instant endDate);


    @Query(value = "SELECT distinct u " +
            "FROM Timetable t " +
            "JOIN t.user u " +
            "WHERE u.departmentUnit IN (:departmentUnits) " +
            "AND t.dateTime BETWEEN :startDate AND :endDate " +
            "AND t.actionType = 'CHECK_IN' " +
            "AND u.id NOT IN (SELECT distinct t2.user.id FROM Timetable t2 " +
            "WHERE t2.actionType = 'CHECK_OUT' " +
            "AND t2.dateTime BETWEEN :startDate AND :endDate)",
            countQuery = "SELECT count(distinct u.id) " +
                    "FROM Timetable t " +
                    "JOIN t.user u " +
                    "WHERE u.departmentUnit IN (:departmentUnits) " +
                    "AND t.dateTime BETWEEN :startDate AND :endDate " +
                    "AND t.actionType = 'CHECK_IN' " +
                    "AND u.id NOT IN (SELECT distinct t2.user.id FROM Timetable t2 " +
                    "WHERE t2.actionType = 'CHECK_OUT' " +
                    "AND t2.dateTime BETWEEN :startDate AND :endDate)")
    Page<User> findAllPresentForDashboard(List<DepartmentUnit> departmentUnits, Pageable pageable, Instant startDate, Instant endDate);

}
