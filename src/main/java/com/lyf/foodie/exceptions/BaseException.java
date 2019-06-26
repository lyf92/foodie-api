package com.lyf.foodie.exceptions;

public class BaseException extends RuntimeException {
    private ErrorCode code;

    public BaseException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
