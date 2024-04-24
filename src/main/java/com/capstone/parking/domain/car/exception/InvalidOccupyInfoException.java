package com.capstone.parking.domain.car.exception;

public class InvalidOccupyInfoException extends RuntimeException{

    private static final String MESSAGE = "위치의 점유 상태가 비정상적입니다.";

    public InvalidOccupyInfoException() {
        super(MESSAGE);
    }

    public InvalidOccupyInfoException(String message) {
        super(message);
    }

    public InvalidOccupyInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOccupyInfoException(Throwable cause) {
        super(cause);
    }

    protected InvalidOccupyInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
