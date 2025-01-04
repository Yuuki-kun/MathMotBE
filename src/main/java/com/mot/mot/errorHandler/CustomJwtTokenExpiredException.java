package com.mot.mot.errorHandler;

public class CustomJwtTokenExpiredException extends RuntimeException {
    public CustomJwtTokenExpiredException(String tokenHasExpired) {
        super(tokenHasExpired);

    }
}
