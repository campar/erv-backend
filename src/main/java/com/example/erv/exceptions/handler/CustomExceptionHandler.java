package com.example.erv.exceptions.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.erv.dataimport.UserImportException;
import com.example.erv.dto.ErrorResponse;
import com.example.erv.exceptions.CircularParentReferenceException;
import com.example.erv.exceptions.DeletedUserException;
import com.example.erv.exceptions.DeviceContainsLocationException;
import com.example.erv.exceptions.UserContainsRoleException;
import com.example.erv.service.departmentunit.DepartmentUnitDeleteException;

@ControllerAdvice
public class CustomExceptionHandler extends DefaultExceptionHandler {
    @ExceptionHandler(DepartmentUnitDeleteException.class)
    public final ResponseEntity<Object> handleDepartmentUnitDeleteException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request", details, DepartmentUnitDeleteException.LOCALIZATION_KEY);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserImportException.class)
    public final ResponseEntity<Object> handleUserImportException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request", details, UserImportException.LOCALIZATION_KEY);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DeviceContainsLocationException.class)
    public final ResponseEntity<Object> handleDeviceContainsLocationException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request", details, DeviceContainsLocationException.LOCALIZATION_KEY);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserContainsRoleException.class)
    public final ResponseEntity<Object> handleUserContainsRoleException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request", details, UserContainsRoleException.LOCALIZATION_KEY);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(CircularParentReferenceException.class)
    public final ResponseEntity<Object> handleCircularParentReferenceException(Exception ex) {
    	logger.error(ex.getMessage());
    	Map<String, Object> details = new HashMap<>();
    	details.put("details", ex.getLocalizedMessage());
    	ErrorResponse error = new ErrorResponse("400", "Bad request", details, CircularParentReferenceException.LOCALIZATION_KEY);
    	return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DeletedUserException.class)
    public final ResponseEntity<Object> handleDeletedUserException(Exception ex) {
    	logger.error(ex.getMessage());
    	Map<String, Object> details = new HashMap<>();
    	details.put("details", ex.getLocalizedMessage());
    	ErrorResponse error = new ErrorResponse("401", "Bad request", details, DeletedUserException.LOCALIZATION_KEY);
    	return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
