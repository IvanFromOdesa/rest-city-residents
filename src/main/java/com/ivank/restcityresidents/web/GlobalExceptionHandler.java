package com.ivank.restcityresidents.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ivank.restcityresidents.exception.LocalDateParseException;
import com.ivank.restcityresidents.exception.NotFoundException;
import com.ivank.restcityresidents.exception.NotValidParamsException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Modified ExceptionHandler from project shown on lesson
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * If the exception was thrown while server was processing the dto.
     * If the dto was processed by server, throw FORBIDDEN to distinct from
     * the ones where server didn't even start.
     * NOTE: the NotFoundException returns with BAD_REQUEST.
     * @param e thrown exception
     * @return response in JSON format
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException thrown: {}", e.getMessage());
        return buildErrorResponse(e.getMessage(), FORBIDDEN);
    }

    /**
     * Validation failed even before server started to process dto.
     * Very detailed response to indicate what has gone wrong.
     * Catches errors in {@link @NotBlank}, {@link @Digits}, {@link @NotEmpty},
     * though not everything - some errors may be processed by standard spring validation.
     * @param e thrown exception
     * @return response in JSON format
     */
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public NotValidParamsException methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException thrown: {}", e.getMessage());
        var result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return NotValidParamsException.processFieldErrors(fieldErrors);
    }

    /**
     * If LocalDate deserialization failed.
     * Used fancy custom deserializer to then catch the exception here.
     * Alternatively, its use can be omitted but the response is less readable.
     * Some other validation e.g. "numbers" is still caught by standard spring validation.
     * @param e parsing exception
     * @return response in JSON format
     */

    @ExceptionHandler(LocalDateParseException.class)
    protected ResponseEntity<Object> handleLocalDateParseException(LocalDateParseException e) {
        log.warn("LocalDateParseException thrown: {}", e.getMessage());
        return buildErrorResponse(e.getMessage(), BAD_REQUEST);
    }

    /**
     * If entity with specified id was not found.
     * @param e not found exception
     * @return response in JSON format
     */
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        log.warn("NotFoundException thrown: {}", e.getMessage());
        return buildErrorResponse(e.getMessage(), BAD_REQUEST);
    }

    private static ResponseEntity<Object> buildErrorResponse(String message, HttpStatus httpStatus) {
        var response = new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), message);
        return ResponseEntity.status(httpStatus.value()).body(response);
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL) // If some fields are null, and we don't want to serialize them
    static class ErrorResponse {
        private int status;
        private String error;
        private String message;
    }
}
