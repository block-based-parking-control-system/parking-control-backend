package com.capstone.parking.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class EntranceRouteInfo {

    private final List<Point> route;

    private final Long parkingLotNum;
}
