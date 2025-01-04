package com.mot.mot.errorHandler;

public class CustomJwtTokenInvalidException extends RuntimeException {
    public CustomJwtTokenInvalidException( String tokenIsInvalid) {
        super(tokenIsInvalid);
    }
}
