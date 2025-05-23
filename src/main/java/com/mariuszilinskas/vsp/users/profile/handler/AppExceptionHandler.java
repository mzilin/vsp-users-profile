package com.mariuszilinskas.vsp.users.profile.handler;

import com.mariuszilinskas.vsp.users.profile.dto.ErrorResponse;
import com.mariuszilinskas.vsp.users.profile.dto.FieldErrorResponse;
import com.mariuszilinskas.vsp.users.profile.exception.EntityExistsException;
import com.mariuszilinskas.vsp.users.profile.exception.FileUploadException;
import com.mariuszilinskas.vsp.users.profile.exception.IncorrectFileException;
import com.mariuszilinskas.vsp.users.profile.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a global exception handler that handles exceptions thrown across the user profile service.
 *
 * @author Marius Zilinskas
 */
@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    // ----------------- Request Validations ----------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        HttpStatus status = HttpStatus.BAD_REQUEST;
        FieldErrorResponse errorResponse = new FieldErrorResponse(errors, status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }

    // --------------------- General ------------------------------

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityExistsException(EntityExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --------------------- Specific -----------------------------

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IncorrectFileException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectFileException(IncorrectFileException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // -----------------------------------------------------------

    /**
     * This method builds the error response for a given exception.
     *
     * @param message the exception message
     * @param status the HTTP status
     * @return a ResponseEntity that includes the error response and the given HTTP status
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        logger.error("Status: {}, Message: '{}'", status.value(), message);
        ErrorResponse errorResponse = new ErrorResponse(message, status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }

}
