package com.example.erv.service.timetable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.erv.config.AppConfig;
import com.example.erv.dto.ArrivalCountDTO;
import com.example.erv.dto.DashboardData;
import com.example.erv.dto.TimetableInfo;
import com.example.erv.dto.UserDashboardData;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.repository.user.UserDailyReportDTO;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.user.UserAttribute;
import com.example.erv.service.user.UserService;
import com.example.erv.specification.SearchDTO;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;
import com.example.erv.utils.EntityUtils;
import com.example.erv.utils.SecurityUtils;

import lombok.Getter;

@Service
public class DashboardDataService {
	@Autowired
	@Getter
	private SecurityUtils securityUtils;
	private final UserService userService;
	private final TimetableService timetableService;
	private final DepartmentUnitService departmentUnitService;

	public DashboardDataService(UserService userService, TimetableService timetableService, DepartmentUnitService departmentUnitService) {
		super();
		this.userService = userService;
		this.timetableService = timetableService;
		this.departmentUnitService = departmentUnitService;
	}

	public DashboardData getDashboardData(SearchDTO request) {
		ZonedDateTime endDate = ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId()));
		ZonedDateTime startDate = ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId())).with(LocalTime.MIN);
		DashboardData dashboardData = new DashboardData();

		dashboardData.setActionCount(timetableService.getActionCount(endDate, startDate));
		List<UserDashboardData> presentUsers = getAllUserData(endDate, startDate);

		dashboardData.setPresentUsers(getUserData(request, endDate, startDate));
		dashboardData.setEmployeesOnBusinessTripCount(countEmployeesOnBusinessTrip(presentUsers));
		dashboardData.setEmployeesOnPauseCount(countEmployeesOnPause(presentUsers));
		dashboardData.setPresentEmployeesCount(countPresentEmployees(presentUsers));
		dashboardData.setTotalCheckIns(countCheckins(presentUsers));
		User user = securityUtils.getCurrentUser();
		List<DepartmentUnit> accessible = departmentUnitService.getAllAccessible(user);
		dashboardData.setArrivalCount(getAttendanceRecords(accessible));
		dashboardData.setLeaveCount(getLeaveRecords(accessible));

		return dashboardData;
	}

	private Page<UserDashboardData> getUserData(SearchDTO request, ZonedDateTime endDate, ZonedDateTime startDate) {
		Page<User> users = timetableService.findAllForDashboard(request.createPageable(), endDate, startDate);
		List<UserDailyReportDTO> data = userService.findDashboardData(users.toList(), endDate, startDate);
		return users.map(user -> {
			List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
			return createUserData(dto, user);
		});
	}

	private List<UserDashboardData> getAllUserData(ZonedDateTime endDate, ZonedDateTime startDate) {
		Page<User> users = timetableService.findAllForDashboard(Pageable.unpaged(), endDate, startDate);
		List<UserDailyReportDTO> data = userService.findDashboardData(users.toList(), endDate, startDate);
		return users.map(user -> {
			List<UserDailyReportDTO> dto = data.stream().filter(e -> e.getUser().getId().equals(user.getId())).toList();
			return createUserData(dto, user);
		}).toList();
	}

	private UserDashboardData createUserData(List<UserDailyReportDTO> dto, User user) {
		List<TimetableInfo> timetables = dto.stream().map(UserDailyReportDTO::getTimetable).sorted(Comparator.comparing(Timetable::getDateTime))
				.map(timetable -> new TimetableInfo(timetable.getActionType(), timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())))).toList();
		return new UserDashboardData(timetables, user);
	}

	public Long countPresentEmployees(List<UserDashboardData> userDashboardDataList) {
		return userDashboardDataList.stream().filter(userDashboardData -> {
			boolean hasCheckIn = userDashboardData.getTimetableInfo().stream().anyMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.CHECK_IN);
			boolean hasNoCheckOut = userDashboardData.getTimetableInfo().stream().noneMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.CHECK_OUT);
			return hasCheckIn && hasNoCheckOut;
		}).count();
	}

	public Long countEmployeesOnPause(List<UserDashboardData> userDashboardDataList) {
		return userDashboardDataList.stream().filter(userDashboardData -> {
			boolean hasPauseStart = userDashboardData.getTimetableInfo().stream().anyMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.PAUSE_START);
			boolean hasNoPauseEnd = userDashboardData.getTimetableInfo().stream().noneMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.PAUSE_END);
			return hasPauseStart && hasNoPauseEnd;
		}).count();
	}

	public Long countEmployeesOnBusinessTrip(List<UserDashboardData> userDashboardDataList) {
		return userDashboardDataList.stream().filter(userDashboardData -> {
			boolean hasBusinessStart = userDashboardData.getTimetableInfo().stream().anyMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.BUSINESS_START);
			boolean hasNoBusinessEnd = userDashboardData.getTimetableInfo().stream().noneMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.BUSINESS_END);
			return hasBusinessStart && hasNoBusinessEnd;
		}).count();
	}

	public Long countCheckins(List<UserDashboardData> userDashboardDataList) {
		return userDashboardDataList.stream().filter(userDashboardData -> userDashboardData.getTimetableInfo().stream().anyMatch(timetableInfo -> timetableInfo.getActionType() == ActionType.CHECK_IN))
				.count();
	}

	private List<ArrivalCountDTO> getAttendanceRecords(List<DepartmentUnit> accessible) {
		SpecificationBasic<Timetable> specification = SpecificationBasic
				.<Timetable>where(
						TimetableAttribute.DATE_TIME,
						SearchOperator.BETWEEN,
						List.of(LocalDate.now().atStartOfDay(), ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId()))
				)
		)
				.joinAnd(TimetableAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(TimetableAttribute.ACTION_TYPE, ActionType.CHECK_IN);
		return getAttendanceRecords(specification);
	}

	private List<ArrivalCountDTO> getAttendanceRecords(SpecificationBasic<Timetable> specification) {
		List<Timetable> checkinData = timetableService.findAll(specification.getSpecification());
		List<ArrivalCountDTO> attendanceRecords = new ArrayList<>();
		EntityUtils.groupByMinutesInterval(checkinData, 1).forEach((intervalStart, groupedTimetables) -> attendanceRecords.add(new ArrivalCountDTO(intervalStart, groupedTimetables.size())));
		return attendanceRecords;
	}

	private List<ArrivalCountDTO> getLeaveRecords(List<DepartmentUnit> accessible) {
		SpecificationBasic<Timetable> specification = SpecificationBasic
				.<Timetable>where(TimetableAttribute.DATE_TIME, SearchOperator.BETWEEN, List.of(LocalDate.now().atStartOfDay(), ZonedDateTime.now(ZoneId.of(AppConfig.getTimezoneId()))))
				.joinAnd(TimetableAttribute.USER, UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible).and(TimetableAttribute.ACTION_TYPE, ActionType.CHECK_OUT);
		return getAttendanceRecords(specification);
	}
}
