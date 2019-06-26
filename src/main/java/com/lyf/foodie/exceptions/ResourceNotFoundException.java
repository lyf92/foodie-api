package com.lyf.foodie.exceptions;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(ErrorCode code, String message) {
        super(code, message);
    }
}
