package com.example.erv.service.user;

import com.example.erv.service.EntityAttribute;

public enum UserAttribute implements EntityAttribute {
    ID("id"),
    USERNAME("username"),
    PASSWORD("password"),
    EMAIL("email"),
    CREATION_DATE("creationDate"),
    MODIFY_DATE("modifyDate"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    PARENT_NAME("parentName"),
    RECORD_NUMBER("recordNumber"),
    BIRTH_DATE("birthDate"),
    JMBG("jmbg"),
    JOB_TITLE("jobTitle"),
    OFFICE_NUMBER("officeNumber"),
    AVATAR("avatar"),
    SOFT_DELETED("softDeleted"),
    USER_TYPE("userType"),
    SHIFTS("shifts"),  // Represents a ManyToMany relationship with the Shift entity
    ROLE("role"),  // Represents a ManyToOne relationship with the Role entity
    CARD_LIST("cardList"),  // Represents a OneToMany relationship with the Card entity
    TIMETABLE_LIST("timetableList"),  // Represents a OneToMany relationship with the Timetable entity
    DEPARTMENT_UNIT("departmentUnit"),  // Represents a ManyToOne relationship with the DepartmentUnit entity
    SETTINGS("settings");  // Represents a OneToMany relationship with the Settings entity

    private final String name;

    UserAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
