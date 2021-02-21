package com.example.boilerplate.exception;

import com.example.boilerplate.util.enums.ErrorCode;
import com.example.boilerplate.util.error.ApiFieldErrorResponse;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class BadRequestException extends RuntimeException {

    private List<? extends ApiFieldErrorResponse> fieldErrors;

    private ErrorCode errorCode = ErrorCode.GENERAL_ERROR;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message, List<? extends ApiFieldErrorResponse> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    protected void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
