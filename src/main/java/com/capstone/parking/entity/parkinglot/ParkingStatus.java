package com.capstone.parking.entity.parkinglot;

public enum ParkingStatus {

    OCCUPIED, //해당 주차 공간에 차가 있을 때
    NON_OCCUPIED, //해당 주차 공간에 차가 없을 때
    TO_BE_OCCUPIED; //해당 주차 공간에 차가 곧 들어올 때(경로를 할당받은 차량이 있을 때)

}
