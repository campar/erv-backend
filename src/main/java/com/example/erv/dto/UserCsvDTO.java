package com.example.erv.dto;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.model.enums.UserType;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserCsvDTO {
    @CsvBindByName(column = "email")
    @Email(message = "Email must be a valid format")
    private String email;

    @CsvBindByName(column = "FirstName")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}' -]{0,127}$",
            message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
    @NotEmpty
    private String firstName;

    @CsvBindByName(column = "LastName")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}' -]{0,127}$",
            message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
    @NotEmpty
    private String lastName;

    @CsvBindByName(column = "ParentName")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}' -]{0,127}$",
            message = "Name must contain only valid letters, spaces, hyphens, or apostrophes.")
    private String parentName;

    @CsvBindByName(column = "RecordNumber")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N}' -]{0,127}$",
            message = "Name must contain only valid letters, numbers, spaces, hyphens, or apostrophes.")
    private String recordNumber;

    @CsvBindByName(column = "JobTitle")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N}' -]{0,127}$",
            message = "Name must contain only valid letters, numbers, spaces, hyphens, or apostrophes.")
    private String jobTitle;

    @CsvBindByName(column = "RFID")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N}' -]{0,127}$",
            message = "Name must contain only valid letters, numbers, spaces, hyphens, or apostrophes.")
    private String rfid;

    @CsvBindByName(column = "DepartmentUnit")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N}' -]{0,127}$",
            message = "Name must contain only valid letters, numbers, spaces, hyphens, or apostrophes.")
    @NotEmpty
    private String departmentUnit;

    public User toEntity(DepartmentUnitService departmentUnitService) {
        User user = new User();
        user.setUserType(UserType.EMPLOYEE);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setParentName(parentName);
        user.setJobTitle(jobTitle);
        user.setEmail(email);
        user.setRecordNumber(recordNumber);
        List<DepartmentUnit> departmentUnits = departmentUnitService.findAllByCode(departmentUnit);
        if (departmentUnits.isEmpty()) throw new EntityNotFoundException("DepartmentUnit");
        user.setDepartmentUnit(departmentUnits.get(0));
        return user;
    }
}
