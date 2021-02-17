package com.example.boilerplate.exception.handler;

import com.example.boilerplate.exception.BadRequestException;
import com.example.boilerplate.exception.ResourceNotFoundException;
import com.example.boilerplate.exception.UserAlreadyExistsException;
import com.example.boilerplate.util.enums.ErrorCode;
import com.example.boilerplate.util.error.ApiErrorResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String WRAPPER_MESSAGE_PREFIX = "{";

    private static final String WRAPPER_MESSAGE_SUFFIX = "}";

    private static final String SINGLE_MESSAGE_PREFIX = "[";

    private static final String SINGLE_MESSAGE_SUFFIX = "]";

    private static final String WRAPPER_MESSAGE_DELIMITER = ", ";

    private static final String FIELD_VALUE_SEPARATOR = ": ";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, createErrorMessage(HttpStatus.BAD_REQUEST, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI()), new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
        HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, createErrorMessage(HttpStatus.BAD_REQUEST, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI()), new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolation(final RuntimeException ex,
        final WebRequest request) {
        return handleExceptionInternal(ex, createErrorMessage(HttpStatus.BAD_REQUEST, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI()), new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex,
        final WebRequest request) {
        return handleExceptionInternal(ex, createErrorMessage(HttpStatus.NOT_FOUND, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI()), new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request);
    }

    @ExceptionHandler({BadRequestException.class, UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleBadRequest(final BadRequestException ex, final WebRequest request) {
        var errorMessage = createErrorMessage(HttpStatus.BAD_REQUEST, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI(), ex.getErrorCode());
        errorMessage.setFieldErrors(ex.getFieldErrors());
        return handleExceptionInternal(ex, errorMessage, new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request);
    }

    @ExceptionHandler({PropertyReferenceException.class})
    public ResponseEntity<Object> handleBadPropertyName(final RuntimeException ex,
        final WebRequest request) {
        return handleExceptionInternal(ex, createErrorMessage(HttpStatus.BAD_REQUEST, ex,
            ((ServletWebRequest) request).getRequest().getRequestURI()), new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request);
    }

    private ApiErrorResponse createErrorMessage(final HttpStatus httpStatus, final Exception ex,
        String path) {
        // TODO: Once we define and cover all error codes remove this method
        return createErrorMessage(httpStatus, ex, path, ErrorCode.UNSPECIFIC_ERROR);
    }

    private ApiErrorResponse createErrorMessage(final HttpStatus httpStatus, final Exception ex,
        String path, ErrorCode errorCode) {
        var error = httpStatus.getReasonPhrase();
        var message = getExceptionMessage(ex);

        log.error(message, ex);

        return new ApiErrorResponse(httpStatus.value(), error, message, path, errorCode);
    }

    private String getExceptionMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            return readValidationErrors((MethodArgumentNotValidException) ex);
        }
        if (ex instanceof DataIntegrityViolationException || ex instanceof BadRequestException) {
            return ex.getLocalizedMessage();
        }

        if (ex instanceof PropertyReferenceException) {
            var e = (PropertyReferenceException) ex;
            return "No property [" + e.getPropertyName()
                + "] found! If you spelled the field name right, make sure it is in the correct case "
                + "(field names are case-sensitive).";
        }

        return "Something went wrong";
    }

    private String readValidationErrors(MethodArgumentNotValidException ex) {
        // We use this when its our custom exception from validator
        // It won't be null for our custom exception
        if (ex.getBindingResult() == null) {
            return ex.getMessage();
        }

        return ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::getValidationErrorDetails)
            .collect(Collectors.joining(WRAPPER_MESSAGE_DELIMITER, WRAPPER_MESSAGE_PREFIX,
                WRAPPER_MESSAGE_SUFFIX));
    }

    private String getValidationErrorDetails(FieldError fieldError) {
        return SINGLE_MESSAGE_PREFIX + fieldError.getField() + FIELD_VALUE_SEPARATOR + fieldError
            .getDefaultMessage()
            + SINGLE_MESSAGE_SUFFIX;
    }
}
