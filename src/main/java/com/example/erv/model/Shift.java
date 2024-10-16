package com.example.erv.model;

import java.time.Duration;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shift")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(nullable = false)
    @Pattern(
    		regexp = "^(?:[01]?\\d|2[0-3]):[0-5]?\\d$",
            message = "Value must be in HH:mm format. Example 12:30"
        )
    private String startFrom;
    @NotEmpty
    @Column
    @Pattern(
    		regexp = "^(?:[01]?\\d|2[0-3]):[0-5]?\\d$",
            message = "Value must be in HH:mm format. Example 12:30"
        )
    private String startTo;
    @NotEmpty
    @Column(nullable = false)
    @Pattern(
    		regexp = "^(?:[01]?\\d|2[0-3]):[0-5]?\\d$",
            message = "Value must be in HH:mm format. Example 12:30"
        )
    private String endFrom;
    @NotEmpty
    @Column
    @Pattern(
    		regexp = "^(?:[01]?\\d|2[0-3]):[0-5]?\\d$",
            message = "Value must be in HH:mm format. Example 12:30"
        )
    private String endTo;
    @NotNull
    @Column(nullable = false)
    @Min(value = 0, message = "Value must be between 0 and 24.")
    @Max(value = 24, message = "Value must be between 0 and 24.")
    private Integer durationHours;
    @NotNull
    @Column(nullable = false)
    @Min(value = 0, message = "Value must be between 0 and 59.")
    @Max(value = 59, message = "Value must be between 0 and 59.")
    private Integer durationMinutes;
    @JsonIgnore
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean softDeleted = false;
    
    @JsonIgnore
    public int getFullDurationInMinutes() {
        return (durationHours * 60) + durationMinutes;
    }

    @JsonIgnore
    public Duration getDuration() {
        return Duration.ofHours(durationHours).plusMinutes(durationMinutes);
    }

    @JsonIgnore
    public LocalTime getStartToTime() {
        return LocalTime.parse(startTo);
    }

    @JsonIgnore
    public LocalTime getStartFromTime() {
        return LocalTime.parse(startFrom);
    }

    @JsonIgnore
    public LocalTime getEndToTime() {
        return LocalTime.parse(endTo);
    }

    @JsonIgnore
    public LocalTime getEndFromTime() {
        return LocalTime.parse(endFrom);
    }

    @JsonIgnore
    @Override
    public String toString() {
        return this.startFrom + " - " + this.endFrom + "(" + this.startTo + " - " + this.endTo + ")";
    }
}
