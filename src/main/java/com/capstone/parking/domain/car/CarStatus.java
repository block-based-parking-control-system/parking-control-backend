package com.capstone.parking.domain;

public enum CarStatus {

    BEFORE_ENTER("입차 전"),
    ENTERING("입차 중"),

    //TODO 필요하면 '주차'를 '입차 완료'와 '출차 전'으로 변경
    PARKED("주차"),

    DEPARTING("출자 중"),
    DEPARTURE_COMPLETE("출차 완료");

    CarStatus(String description) {

    }
}
