package com.example.erv.model;

import java.time.Instant;

import com.example.erv.model.enums.ActionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "timetable")
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @NotNull
    @Column(nullable = false)
    private Instant dateTime;
    @ManyToOne
    @JoinColumn(name = "erv_user", referencedColumnName = "id")
    private User user;

    @Column
    private Long groupingKey;

    @ManyToOne
    @JoinColumn(name = "shift", referencedColumnName = "id")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "device", referencedColumnName = "id")
    private Device device;

    @PostPersist
    public void postPersist() {
        if (this.actionType == ActionType.CHECK_IN) {
            this.groupingKey = this.id;
        }
    }
}
