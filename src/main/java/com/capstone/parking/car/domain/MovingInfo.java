package com.capstone.parking.car.domain;

import lombok.Getter;

import java.awt.*;
import java.util.List;

@Getter
public class MovingInfo {

    private List<Point> route; //이동 경로

    private Integer expectedTime; //예상 소요 시간

    private Point currentLocation; //현재 위치(TODO 포인트 하나로 할지, 겹치는 경우를 고려해서 두개로 할지)

    public MovingInfo(List<Point> route) {
        this.route = route;
        this.currentLocation = route.get(0);
        calculateExpectedTime(route);
    }

    //TODO route 로부터 예상 소요 시간 계산
    private void calculateExpectedTime(List<Point> route) {

    }

    public void updateLocation(Point nextLocation) {
        if (!isAdjacent(currentLocation, nextLocation)) {
            //TODO 예외 발생
        }
        if (!route.contains(nextLocation)) {
            //TODO 예외 발생
        }

        currentLocation = nextLocation;
    }

    private boolean isAdjacent(Point current, Point next) {
        return (Math.abs(current.getX() - next.getX()) == 1 && current.getY() == next.getY())
                || (Math.abs(current.getY() - next.getY()) == 1 && current.getX() == next.getX());
    }
}
