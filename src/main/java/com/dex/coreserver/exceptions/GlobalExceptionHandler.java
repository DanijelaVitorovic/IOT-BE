package com.dex.coreserver.exceptions;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public static ResponseEntity<ErrorObject> handleResourceNotFoundException(AppResourceNotFoundException ex){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setMessage(ex.getMessage());
        errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorObject.setRequestId(ThreadContext.get("requestId"));
        return new ResponseEntity<>(errorObject,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public static ResponseEntity<ErrorObject> handleNoDataFoundException(AppNoDataFoundException ex){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setMessage(ex.getMessage());
        errorObject.setStatusCode(HttpStatus.NO_CONTENT.value());
        errorObject.setRequestId(ThreadContext.get("requestId"));
        return new ResponseEntity<>(errorObject,HttpStatus.OK);
    }

    @ExceptionHandler
    public static ResponseEntity<ErrorObject> handleConstraintViolationException(AppDataIntegrityViolationException ex){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setMessage(ex.getMessage());
        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setRequestId(ThreadContext.get("requestId"));
        return new ResponseEntity<>(errorObject,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public static ResponseEntity<ErrorObject> globalExceptionHandler(Exception ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setMessage(ex.getMessage());
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setRequestId(ThreadContext.get("requestId"));
        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppException.class)
    public static ResponseEntity<ErrorObject> globalExceptionHandler(AppException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setMessage(ex.getMessage());
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setRequestId(ThreadContext.get("requestId"));
        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
