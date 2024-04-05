package com.capstone.parking.car.domain;

public enum CarStatus {

    BEFORE_ENTER("입차 전"),
    ENTERING("입차 중"),

    //TODO '입차 완료'와 '출차 전' 을 '주차'로 통일?
    ENTRY_COMPLETE("입차 완료"),
    BEFORE_DEPARTURE("출차 전"),
    DEPARTING("출자 중"),
    DEPARTURE_COMPLETE("출차 완료");

    CarStatus(String description) {

    }
}
