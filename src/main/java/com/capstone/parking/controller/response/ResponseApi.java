package com.capstone.parking.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ResponseApi<T> {

    private final Boolean success;

    private final T data;
}
