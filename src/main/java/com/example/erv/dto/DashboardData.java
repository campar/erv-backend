package com.example.erv.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class DashboardData {
	private Long totalCheckIns;
	private Long presentEmployeesCount;
	private Long employeesOnPauseCount;
	private Long employeesOnBusinessTripCount;
	private ActionCount actionCount;
	private Page<UserDashboardData> presentUsers;
	private List<ArrivalCountDTO> arrivalCount;
	private List<ArrivalCountDTO> leaveCount;

	public void setPresentEmployeesCount(Long presentEmployeesCount) {
		this.presentEmployeesCount = presentEmployeesCount == null ? 0 : presentEmployeesCount;
	}

	public void setEmployeesOnPauseCount(Long employeesOnPauseCount) {
		this.employeesOnPauseCount = employeesOnPauseCount == null ? 0 : employeesOnPauseCount;
	}

	public void setEmployeesOnBusinessTripCount(Long employeesOnBusinessTripCount) {
		this.employeesOnBusinessTripCount = employeesOnBusinessTripCount == null ? 0 : employeesOnBusinessTripCount;
	}

	public void setActionCount(ActionCount actionCount) {
		this.actionCount = actionCount;
	}

	public void setPresentUsers(Page<UserDashboardData> presentUsers) {
		this.presentUsers = presentUsers;
	}

	public void setTotalCheckIns(Long totalCheckIns) {
		this.totalCheckIns = totalCheckIns == null ? 0 : totalCheckIns;
	}

	public void setArrivalCount(List<ArrivalCountDTO> arrivalCount) {
		this.arrivalCount = arrivalCount;
	}

	public void setLeaveCount(List<ArrivalCountDTO> leaveCount) {
		this.leaveCount = leaveCount;
	}

}
