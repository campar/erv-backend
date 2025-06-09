package com.example.erv.service.user.reports;

import com.example.erv.model.Timetable;
import com.example.erv.model.enums.ActionType;
import lombok.Data;

@Data
public class TimetableActionData {
    private ActionType actionType;
    private Timetable timetable;
    private boolean valid = true;

    public TimetableActionData(ActionType actionType, Timetable timetable) {
        this.timetable = timetable;
        this.actionType = actionType;
    }
}
