package com.example.erv.repository.user;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.erv.model.DepartmentUnit;

@Repository
public interface UserRepository extends UserRepositoryBasic {

    @Query(value = "SELECT DISTINCT new com.example.erv.repository.user.UserDailyReportDTO(u, t) " +
            "FROM User u " +
            "JOIN u.timetableList t " +
            "JOIN u.timetableList t2 on (t.groupingKey = t2.groupingKey) " +
            "LEFT JOIN u.role r " +
            "LEFT JOIN r.permissionList p " +
            "WHERE t.groupingKey IN ( " +
            "   SELECT x.groupingKey " +
            "   FROM Timetable x" +
            "   WHERE x.actionType = 'CHECK_IN'" +
            "   AND x.dateTime BETWEEN :startDate AND :endDate) " +
            "AND (:users IS NULL OR u.id in :users) " +
            "AND (:recordNumber IS NULL OR u.recordNumber = :recordNumber) " +
            "AND u.departmentUnit IN (:departmentUnit) " +
            "AND (u.departmentUnit IN (:accessible) OR p.name = 'super_user') " +
            "AND (u.softDeleted = false OR (u.softDeleted = true AND (u.modifyDate > :startDate))) " +
            "ORDER BY t.dateTime desc",
            countQuery = "SELECT count(distinct u.id) " +
                    "FROM User u " +
                    "JOIN u.timetableList t " +
                    "JOIN u.timetableList t2 " +
                    "LEFT JOIN u.role r " +
                    "LEFT JOIN r.permissionList p " +
                    "WHERE t.groupingKey IN ( " +
                    "   SELECT x.groupingKey " +
                    "   FROM Timetable x" +
                    "   WHERE x.actionType = 'CHECK_IN'" +
                    "   AND x.dateTime BETWEEN :startDate AND :endDate" +
                    "   AND x.user.id = u.id) " +
                    "AND (:users IS NULL OR u.id in :users) " +
                    "AND (:recordNumber IS NULL OR u.recordNumber = :recordNumber) " +
                    "AND u.departmentUnit IN (:departmentUnit) " +
                    "AND (u.departmentUnit IN (:accessible) OR p.name = 'super_user') " +
            		"AND (u.softDeleted = false OR (u.softDeleted = true AND (u.modifyDate > :startDate)))")
    List<UserDailyReportDTO> findReportData(Instant startDate, Instant endDate, List<Long> users, String recordNumber, List<DepartmentUnit> departmentUnit, List<DepartmentUnit> accessible);

    @Query(value = "SELECT new com.example.erv.repository.user.UserDailyReportDTO(u, t) " +
            "FROM User u " +
            "JOIN u.timetableList t " +
            "WHERE (t.dateTime BETWEEN :startDate AND :endDate) " +
            "AND (:users IS NULL OR u.id in :users) " +
            "AND u.departmentUnit IN (:departmentUnits) " +
            "ORDER BY t.dateTime desc",
            countQuery = "SELECT  count(distinct u.id) " +
                    "FROM User u " +
                    "JOIN u.timetableList t " +
                    "WHERE (t.dateTime BETWEEN :startDate AND :endDate) " +
                    "AND (:users IS NULL OR u.id in :users) " +
                    "AND u.departmentUnit IN (:departmentUnits)")
    List<UserDailyReportDTO> findUserDashboardData(Instant startDate, Instant endDate, List<Long> users, List<DepartmentUnit> departmentUnits);
    
    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserIds();
}
