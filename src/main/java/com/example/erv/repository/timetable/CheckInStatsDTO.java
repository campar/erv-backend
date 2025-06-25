package com.example.erv.repository.timetable;

import com.example.erv.model.Device;
import com.example.erv.model.enums.ActionType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckInStatsDTO {
    private Long id;
    private String code;
    private String locationName;
    private String locationCity;
    private String locationAddress;
    private ActionType actionType;
    private Long count;

    public CheckInStatsDTO(Device device, ActionType actionType, Long count) {
        this.id = device.getId();
        this.code = device.getCode();
        this.locationName = device.getLocation().getName();
        this.locationCity = device.getLocation().getCity();
        this.locationAddress = device.getLocation().getAddress();
        this.actionType = actionType;
        this.count = count;
    }
}
