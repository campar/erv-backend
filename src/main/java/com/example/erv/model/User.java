package com.example.erv.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.erv.model.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "erv_user")
@EntityListeners(AuditingEntityListener.class)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String username;
	@Column
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s])[A-Za-z\\d\\W]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
	private String password;
	@Column
	@Email(message = "Email must be a valid format")
	private String email;
	@Column
	@CreatedDate
	private Instant creationDate;
	@Column
	@LastModifiedDate
	private Instant modifyDate;
	@NotEmpty
	@Column(nullable = false)
	@Size(max = 128, message = "First name must not exceed 128 characters.")
	@Pattern(regexp = "^[\\p{L}][\\p{L}' -]{0,127}$", message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
	private String firstName;
	@NotEmpty
	@Column(nullable = false)
	@Size(max = 128, message = "Last name must not exceed 128 characters.")
	@Pattern(regexp = "^[\\p{L}][\\p{L}' -]{0,127}$", message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
	private String lastName;
	@Column
	@Size(max = 128, message = "Parent name must not exceed 128 characters.")
	@Pattern(regexp = "^[\\p{L}][\\p{L}' .-]{0,127}$", message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
	private String parentName;
	@Column(unique = true)
	private String recordNumber;
	@Column
	private LocalDate birthDate;
	@Column(unique = true)
	private String jmbg;
	@Column
	private String jobTitle;
	@Column
	private String officeNumber;
	@Column
	private String avatar;
	@Column(nullable = false, columnDefinition = "boolean default false")
	private Boolean softDeleted = false;
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserType userType;

	@ManyToMany
	@JoinTable(name = "user_shift", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "shift_id"))
	private Set<Shift> shifts = new LinkedHashSet<>();

	@ManyToOne
	@JoinColumn(name = "role", referencedColumnName = "id")
	private Role role;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	List<Card> cardList = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	List<Timetable> timetableList = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "departmentUnit", referencedColumnName = "id")
	private DepartmentUnit departmentUnit;

	@OneToMany(mappedBy = "user")
	@JsonIgnore
	List<Settings> settings = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "user_admin_accessible_department_unit", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "department_unit_id"))
	Set<DepartmentUnit> adminAccessibleDepartmentUnits = new LinkedHashSet<>();
}
