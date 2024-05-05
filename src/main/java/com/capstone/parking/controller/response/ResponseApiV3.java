package com.capstone.parking.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class ResponseApiV3<T> {

    private final Boolean success;

    private final ResponseType type;

    private final T data;

    public static <T> ResponseApiV3<T> of(Boolean success, ResponseType type, T data) {
        return new ResponseApiV3<>(success, type, data);
    }

}
