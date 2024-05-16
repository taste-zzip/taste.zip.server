package com.taste.zip.tastezip.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * TODO e.printStackTrace()를 Discord, Logger 등으로 대체하여 배포환경에서 에러를 관리할 수 있도록 변경해야함.
 */
@RestControllerAdvice
public class ControllerExceptionAdvice {

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    protected ErrorResponse error500(Exception e) {
        e.printStackTrace();
        return ErrorResponse
            .builder(e, HttpStatus.INTERNAL_SERVER_ERROR, "internal server error")
            .build();
    }

    /**
     * @see HttpClientErrorException
     */
    @ExceptionHandler({ HttpClientErrorException.class })
    protected ErrorResponse httpError(HttpClientErrorException e) {
        e.printStackTrace();
        return ErrorResponse
            .create(e, e.getStatusCode(), e.getLocalizedMessage());
    }

    /**
     * Catch com.fasterxml.jackson serialization error
     */
    @ExceptionHandler({ com.fasterxml.jackson.databind.exc.InvalidFormatException.class, org.springframework.http.converter.HttpMessageNotReadableException.class })
    protected ErrorResponse serializationError(com.fasterxml.jackson.databind.exc.InvalidFormatException e1, org.springframework.http.converter.HttpMessageNotReadableException e2) {
        e2.printStackTrace();
        return ErrorResponse
            .create(e2, HttpStatus.BAD_REQUEST, e2.getLocalizedMessage());
    }


    /**
     * Catch validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            stringBuilder
                .append("Field: ")
                .append(fieldName)
                .append(", Error: ")
                .append(errorMessage)
                .append("\n");
        });
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, stringBuilder.toString());
    }

    /**
     * Catch org.springframework.web.bind.MissingServletRequestParameterException which @RequestParam trigger
     */
    @ExceptionHandler({ org.springframework.web.bind.MissingServletRequestParameterException.class })
    protected ErrorResponse parameter(org.springframework.web.bind.MissingServletRequestParameterException e) {
        return ErrorResponse
            .create(e, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
    }
}
