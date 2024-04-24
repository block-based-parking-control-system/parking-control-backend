package com.capstone.parking.domain.car.exception;

public class InvalidCarStatusException extends RuntimeException{

    private static final String MESSAGE = "차량의 상태가 유효하지 않습니다.";

    public InvalidCarStatusException() {
        super(MESSAGE);
    }

    public InvalidCarStatusException(String message) {
        super(message);
    }

    public InvalidCarStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCarStatusException(Throwable cause) {
        super(cause);
    }

    protected InvalidCarStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
