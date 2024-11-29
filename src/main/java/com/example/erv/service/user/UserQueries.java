package com.example.erv.service.user;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.User;
import com.example.erv.service.permission.PermissionAttribute;
import com.example.erv.service.role.RoleAttribute;
import com.example.erv.specification.SearchOperator;
import com.example.erv.specification.SpecificationBasic;

public class UserQueries {

    private UserQueries() {
        // prevents instantiation
    }

    public static Specification<User> findAllForDailyReport(String recordNumber, List<DepartmentUnit> departmentUnit, User user, List<DepartmentUnit> accessible) {
        // Create the base specification
        SpecificationBasic<User> spec = SpecificationBasic.get();

        // 1. Handle (user is null or user matches)
        if (user != null) {
            spec = spec.and(UserAttribute.ID, SearchOperator.EQUAL, user.getId());
        }

        // 2. Handle (recordNumber is null or matches)
        if (recordNumber != null) {
            spec = spec.and(UserAttribute.RECORD_NUMBER, SearchOperator.EQUAL, recordNumber);
        }

        // 3. Handle (departmentUnit IN)
        spec = spec.and(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, departmentUnit);

        // 4. Handle (departmentUnit IN or permission = 'super_user')
        SpecificationBasic<User> departmentUnitOrSuperUserSpec = SpecificationBasic
                .<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible)
                .nestedLeftJoinOr(UserAttribute.ROLE, RoleAttribute.PERMISSION_LIST, PermissionAttribute.NAME, SearchOperator.EQUAL, "super_user");

        // Combine all specifications
        spec = spec.and(departmentUnitOrSuperUserSpec).distinct();

        return spec.getSpecification();
    }
    
    public static Specification<User> findAllForDailyReport(String recordNumber, List<DepartmentUnit> departmentUnit, User user, List<DepartmentUnit> accessible, ZonedDateTime reportStartDate) {
    	// Create the base specification
    	SpecificationBasic<User> spec = SpecificationBasic.get();
    	
    	// 1. Handle (user is null or user matches)
    	if (user != null) {
    		spec = spec.and(UserAttribute.ID, SearchOperator.EQUAL, user.getId());
    	}
    	
    	// 2. Handle (recordNumber is null or matches)
    	if (recordNumber != null) {
    		spec = spec.and(UserAttribute.RECORD_NUMBER, SearchOperator.EQUAL, recordNumber);
    	}
    	
    	// 3. Handle (departmentUnit IN)
    	spec = spec.and(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, departmentUnit);
    	
    	// 4. Handle (departmentUnit IN or permission = 'super_user')
    	SpecificationBasic<User> departmentUnitOrSuperUserSpec = SpecificationBasic
    			.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible)
    			.nestedLeftJoinOr(UserAttribute.ROLE, RoleAttribute.PERMISSION_LIST, PermissionAttribute.NAME, SearchOperator.EQUAL, "super_user");
    	
    	// 5. Add soft-delete filtering:
        // Include:
        // - Non-soft-deleted users OR
        // - Soft-deleted users modified after report start date
        SpecificationBasic<User> softDeleteSpec = SpecificationBasic
                .<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, false)
                .or(
                    SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, true)
                        .and(UserAttribute.MODIFY_DATE, SearchOperator.GTE, reportStartDate)
                );
    	// Combine all specifications
    	spec = spec.and(departmentUnitOrSuperUserSpec).and(softDeleteSpec).distinct();
    	
    	return spec.getSpecification();
    }

    public static Specification<User> findAllNonMandatoryDepartmentUnitAndUser(List<DepartmentUnit> departmentUnit, User user, List<DepartmentUnit> accessible) {
        // Create the base specification
        SpecificationBasic<User> spec = SpecificationBasic.get();

        // 1. Handle (user is null or user matches)
        if (user != null) {
            spec = spec.and(UserAttribute.ID, SearchOperator.EQUAL, user.getId());
        }

        // 2. Handle (u.departmentUnit IN :departmentUnit)
        spec = spec.and(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, departmentUnit);

        // 3. Handle (u.departmentUnit IN :departmentUnits)
        spec = spec.and(SpecificationBasic
                .<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible)
                .nestedLeftJoinOr(UserAttribute.ROLE, RoleAttribute.PERMISSION_LIST, PermissionAttribute.NAME, SearchOperator.EQUAL, "super_user"));

        return spec.distinct().getSpecification();
    }
    
    public static Specification<User> findAllNonMandatoryDepartmentUnitAndUser(List<DepartmentUnit> departmentUnit, User user, List<DepartmentUnit> accessible, ZonedDateTime reportStartDate) {
    	// Create the base specification
    	SpecificationBasic<User> spec = SpecificationBasic.get();
    	
    	// 1. Handle (user is null or user matches)
    	if (user != null) {
    		spec = spec.and(UserAttribute.ID, SearchOperator.EQUAL, user.getId());
    	}
    	
    	// 2. Handle (u.departmentUnit IN :departmentUnit)
    	spec = spec.and(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, departmentUnit);
    	
    	// 3. Handle (u.departmentUnit IN :departmentUnits)
    	spec = spec.and(SpecificationBasic
    			.<User>where(UserAttribute.DEPARTMENT_UNIT, SearchOperator.IN, accessible)
    			.nestedLeftJoinOr(UserAttribute.ROLE, RoleAttribute.PERMISSION_LIST, PermissionAttribute.NAME, SearchOperator.EQUAL, "super_user"));
    	
    	// 5. Add soft-delete filtering:
        // Include:
        // - Non-soft-deleted users OR
        // - Soft-deleted users modified after report start date
        SpecificationBasic<User> softDeleteSpec = SpecificationBasic
                .<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, false)
                .or(
                    SpecificationBasic.<User>where(UserAttribute.SOFT_DELETED, SearchOperator.EQUAL, true)
                        .and(UserAttribute.MODIFY_DATE, SearchOperator.GTE, reportStartDate)
                );
        
    	return spec.and(softDeleteSpec).distinct().getSpecification();
    }
}
