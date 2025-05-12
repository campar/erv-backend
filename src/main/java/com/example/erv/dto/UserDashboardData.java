package com.example.erv.dto;

import java.util.List;

import com.example.erv.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardData {
	private List<TimetableInfo> timetableInfo;
	private User user;
}
