package com.capstone.parking.car.domain;

public class Car {

    private CarType type; //차종 (소형, 중형, 대형)

    private CarStatus status; //차량의 현재 상태 (입차 전, 입차 중, 입차 완료, 출차 전, 출차 중, 출차 완료)

    private MovingInfo movingInfo; //이동 관련 정보(이동 경로, 예상 소요 시간, 현재 위치 등)

}
