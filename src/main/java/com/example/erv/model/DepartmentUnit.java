package com.example.erv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "departmentUnit")
public class DepartmentUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(nullable = false)
    private String name;
    @Column
    private String code;
    @Column
    private String description;
    @OneToMany(mappedBy = "departmentUnit")
    @JsonIgnore
    List<User> userList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "departmentUnit", referencedColumnName = "id")
    private DepartmentUnit parent;
	@JsonIgnore
	@Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean softDeleted = false;
}
