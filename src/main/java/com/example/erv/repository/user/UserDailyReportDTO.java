package com.example.erv.repository.user;

import com.example.erv.model.Timetable;
import com.example.erv.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDailyReportDTO {
    private User user;
    private Timetable timetable;
}
