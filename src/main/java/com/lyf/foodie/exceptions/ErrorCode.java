package com.lyf.foodie.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ErrorCode {
    VIDEO_IS_NOT_FOUND(NOT_FOUND),
    DELETE_VIDEO_FAILED(INTERNAL_SERVER_ERROR),
    VIDEO_IS_EXISTED(BAD_REQUEST),
    UPLOAD_VIDEO_TO_SERVER_FAILED(INTERNAL_SERVER_ERROR),

    USER_IS_NOT_FOUND(NOT_FOUND);

    private HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }
}
