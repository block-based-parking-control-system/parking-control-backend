package com.capstone.parking.service.dto;

import lombok.Getter;

import java.awt.*;
import java.util.List;

@Getter
public class EntranceRouteInfo {

    private final List<Point> route;

    private final Long parkingLotNum;

    public EntranceRouteInfo(List<Point> route, Long parkingLotNum) {
        this.route = route;
        this.parkingLotNum = parkingLotNum;
    }
}
