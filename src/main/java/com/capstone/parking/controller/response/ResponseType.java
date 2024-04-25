package com.capstone.parking.controller.response;

/**
 * ResponseApi 의 data 타입
 */
public enum ResponseType {
    ENTRANCE_ROUTE, //입차 정보 (EntranceRouteInfo)
    EXIT_ROUTE, //출차 정보 (List<Point>)
    CURRENT_LOCATION_SINGLE, //현재 위치 (Point)
    CURRENT_LOCATION_DOUBLE, //현재 위치 (size 가 2인 List<Point>)
    LAST_EVENT, //입차 또는 출차가 성공했는지 여부 (Boolean)
    FAILURE
}
