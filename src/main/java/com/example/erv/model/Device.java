package com.example.erv.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(unique = false)
    private String code;
    @Column(unique = true)
    private String hardwareIdentifier;
    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;
    @Column
    private String description;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean softDeleted = false;
    @ManyToOne(optional = false)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private Location location;
    @Column
    private Instant lastOnlineAt;
    @Column
    private String lastKnownIpAddress;

    @OneToMany(mappedBy = "device")
    @JsonIgnore
    List<Timetable> timetableList = new ArrayList<>();
}
