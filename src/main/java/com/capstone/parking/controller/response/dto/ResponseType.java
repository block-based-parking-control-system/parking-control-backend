package com.capstone.parking.controller.response.dto;

import lombok.Getter;

@Getter
public enum ResponseType {

    ENTRANCE_ROUTE(1),
    EXIT_ROUTE(2),
    CURRENT_LOCATION(3),
    COMPLETION(4);

    private final int value;

    ResponseType(int value) {
        this.value = value;
    }
}
