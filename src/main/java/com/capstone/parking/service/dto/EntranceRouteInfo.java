package com.capstone.parking.service.dto;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EntranceRouteInfo {

    private final List<Point> route;

    private final Long parkingLotNum;

    private List<Integer> currentLocation;

    private Boolean isOccupiedDouble;

    public EntranceRouteInfo(List<Point> route, Long parkingLotNum) {
        this.route = route;
        this.parkingLotNum = parkingLotNum;
        this.currentLocation = new ArrayList<>(2);

        currentLocation.add(0);
    }
}
