package com.capstone.parking.controller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseApi<T> {

    private final Boolean success;

    private final T data;
}
