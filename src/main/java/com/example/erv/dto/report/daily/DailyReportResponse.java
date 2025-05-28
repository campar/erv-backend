package com.example.erv.dto.report.daily;

import java.util.List;

import com.example.erv.model.Timetable;
import com.example.erv.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportResponse {
    private User user;
    private List<Timetable> timetables;
    private String timeSpent;
    private String overtime;

    public DailyReportResponse(User user, List<Timetable> timetables) {
        setUser(user);
        setTimetables(timetables);
    }
}
