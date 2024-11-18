package com.example.erv.exceptions.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.erv.dto.ErrorResponse;
import com.example.erv.dto.PasswordMismatchException;
import com.example.erv.exceptions.BlockedUserException;
import com.example.erv.exceptions.FieldNotFoundException;
import com.example.erv.exceptions.InvalidActionException;
import com.example.erv.exceptions.LocalizationKey;
import com.example.erv.exceptions.ErvException;
import com.example.erv.exceptions.NoHardwareIdentifierException;
import com.example.erv.exceptions.OperationNotSupportedException;
import com.example.erv.service.card.InvalidCardException;
import com.example.erv.service.timetable.UserEmptyShiftsException;

import org.hibernate.exception.ConstraintViolationException;
import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @SuppressWarnings("unused")
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request!", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request!", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PropertyReferenceException.class, FieldNotFoundException.class, InvalidActionException.class})
    public final ResponseEntity<Object> handleClientRequestNotValid(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error;
        if(ex instanceof InvalidActionException invalidActionException) {        	
        	error = new ErrorResponse("400", "Bad request!", details, invalidActionException.getLocalizationMessage());
        }else {
        	error = new ErrorResponse("400", "Bad request!", details);
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public final ResponseEntity<Object> handleDataIntegrityViolationException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        if(ex.getCause() instanceof ConstraintViolationException constraintViolationException) {
        	details.put("attribute", constraintViolationException.getConstraintName().split("_")[1]);
        	details.put("entity", constraintViolationException.getConstraintName().split("_")[0]);
        }
        ErrorResponse error = new ErrorResponse("400", ex.getMessage(), details, LocalizationKey.UNIQUE_VALUE_CONSTRAINT);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public final ResponseEntity<Object> handleResourceNotFound(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("404", "Not Found", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NoHardwareIdentifierException.class)
    public final ResponseEntity<Object> handleNoHardwareIdentifierException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("400", "Bad request", details, LocalizationKey.NO_DEVICE_IDENTIFIER);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidCardException.class)
    public final ResponseEntity<Object> handleInvalidCardException(Exception ex) {
    	logger.error(ex.getMessage());
    	Map<String, Object> details = new HashMap<>();
    	details.put("details", ex.getLocalizedMessage());
    	ErrorResponse error = new ErrorResponse("400", "Bad request", details, LocalizationKey.INVALID_CARD);
    	return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserEmptyShiftsException.class)
    public final ResponseEntity<Object> handleUserEmptyShiftsException(Exception ex) {
    	logger.error(ex.getMessage());
    	Map<String, Object> details = new HashMap<>();
    	details.put("details", ex.getLocalizedMessage());
    	ErrorResponse error = new ErrorResponse("400", "Bad request", details, LocalizationKey.NO_USER_SHIFT_ASSIGNED);
    	return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, ErvException.class})
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("500", "Server Error", details);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public final ResponseEntity<Object> handleConstraintViolation(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("422", "Unprocessable entity! Validation failed.", details);
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({EntityNotFoundException.class, EmptyResultDataAccessException.class})
    public final ResponseEntity<Object> handleEntityNotFound(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("404", "Not found!", details);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OperationNotSupportedException.class)
    public final ResponseEntity<Object> handleOperationNotSupportedException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("418", "I am a teapot!", details);
        return new ResponseEntity<>(error, HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentialsException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("401", "Bad credentials!", details, LocalizationKey.BAD_CREDENTIALS);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
        logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("details", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("403", "Access denied! You do not have permission for this action.", details, LocalizationKey.ACCESS_DENIED);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(BlockedUserException.class)
    public ResponseEntity<?> handleBlockedUserException(BlockedUserException ex) {
    	logger.error(ex.getMessage());
        Map<String, Object> details = new HashMap<>();
        details.put("message", ex.getLocalizedMessage());
        details.put("remainingSeconds", ex.getRemainingSeconds());
        ErrorResponse error = new ErrorResponse("429", "Too many requests!", details, LocalizationKey.TOO_MANY_LOGIN_ATTEMPTS);
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }
    
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<?> handlePasswordMismatchException(PasswordMismatchException ex) {
    	logger.error(ex.getMessage());
    	Map<String, Object> details = new HashMap<>();
    	details.put("message", ex.getLocalizedMessage());
    	ErrorResponse error = new ErrorResponse("400", "Current password don't match.", details, LocalizationKey.CURRENT_PASSWORD_MISMATCH);
    	return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}