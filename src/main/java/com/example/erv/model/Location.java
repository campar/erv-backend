package com.example.erv.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "location")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	@Column(nullable = false)
	private String name;
	@NotEmpty
	@Column(nullable = false)
	private String address;
	@NotEmpty
	@Column(nullable = false)
	private String city;
	@Column
	private String phone;
	@Column
	private String description;
	@JsonIgnore
	@Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean softDeleted = false;
	@OneToMany(mappedBy = "location", cascade = CascadeType.REFRESH)
	@JsonIgnore
	List<Device> deviceList = new ArrayList<>();
}
