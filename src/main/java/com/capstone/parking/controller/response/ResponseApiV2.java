package com.capstone.parking.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseApiV2<T> {

    private final ResponseType type;

    private final T data;

    /*public static <T> ResponseApiV2<T> entranceRoute(T data) {
        return new ResponseApiV2<>(ResponseType.ENTRANCE_ROUTE, data);
    }

    public static <T> ResponseApiV2<T> exitRoute(T data) {
        return new ResponseApiV2<>(ResponseType.EXIT_ROUTE, data);
    }

    public static <T> ResponseApiV2<T> currentLocation(T data) {
        return new ResponseApiV2<>(ResponseType.CURRENT_LOCATION, data);
    }

    public static <T> ResponseApiV2<T> lastEvent(T data) {
        return new ResponseApiV2<>(ResponseType.LAST_EVENT, data);
    }

    public static <T> ResponseApiV2<T> failure(T data) {
        return new ResponseApiV2<>(ResponseType.FAILURE, data);
    }*/
}
