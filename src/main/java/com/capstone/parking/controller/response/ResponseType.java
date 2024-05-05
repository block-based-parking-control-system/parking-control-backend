package com.capstone.parking.controller.response;

import lombok.RequiredArgsConstructor;

/**
 * ResponseApi 의 data 타입
 */
@RequiredArgsConstructor
public enum ResponseType {
    ENTRANCE_ROUTE("Entrance Route"), //입차 정보 (EntranceRouteInfo)
    EXIT_ROUTE("Exit Route"), //출차 정보 (List<Point>)
    CURRENT_SINGLE_LOCATION("Current Single Location"), //현재 위치 (Point)
    CURRENT_DOUBLE_LOCATION("Current Double Location"), //현재 위치 (size 가 2인 List<Point>)
    LAST_EVENT("Last Event"), //입차 또는 출차가 성공했는지 여부 (Boolean)
    FAILURE("Failure");

    private final String description;

    public String getDescription() {
        return description;
    }
}
