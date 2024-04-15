package com.capstone.parking.car.domain;

import com.capstone.parking.car.domain.exception.InvalidOccupyInfoException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.awt.*;
import java.util.List;

@Getter
public class MovingInfo {

    private List<Point> route; //이동 경로

    private Integer expectedTime; //예상 소요 시간

    private Location currentLocation; //현재 위치
    private Location nextLocation; //다음 위치
    private int nextLocationIdx;

    public MovingInfo(List<Point> route) {
        this.route = route;
        this.nextLocationIdx = 1;

        //TODO 예외 처리? (route 리스트에 0,1 번 원소가 없는 경우)
        this.currentLocation = new Location(route.get(0), true);
        this.nextLocation = new Location(route.get(nextLocationIdx), false);
        calculateExpectedTime(route);
    }

    //TODO route 로부터 예상 소요 시간 계산
    private void calculateExpectedTime(List<Point> route) {

    }

    /**
     * 차량이 두 개의 구간을 점유하기 시작할 때 호출
     */
    public void occupyDoubleLocation() {
        if (nextLocation == Location.END_POINT) {
            throw new InvalidOccupyInfoException();
        }
        nextLocation.changeToBeOccupied();
    }

    /**
     * 두 구간을 점유하고 있는 차량이 뒤쪽 구간을 완전히 빠져나올 때 호출
     */
    public void moveForward() {
        currentLocation = nextLocation;

        if (nextLocationIdx < route.size() - 1) {
            nextLocation = new Location(route.get(++nextLocationIdx), false);
        } else {
            nextLocation = Location.END_POINT;
        }

        //TODO 이렇게 한 칸 이동할 때마다 도착 예상 시작을 재계산하는 것이 좋을 듯 (초 단위로 하지 말고)
    }

    /*
    private boolean isAdjacent(Point current, Point next) {
        return (Math.abs(current.getX() - next.getX()) == 1 && current.getY() == next.getY())
                || (Math.abs(current.getY() - next.getY()) == 1 && current.getX() == next.getX());
    }
    */

    @Getter
    static class Location {

        static final Location END_POINT = new Location();

        private Point point;
        private Boolean occupied;

        private Location() {
            this.point = null;
            this.occupied = null;
        }

        Location(@NotNull Point point, @NotNull Boolean occupied) {
            this.point = point;
            this.occupied = occupied;
        }

        void changeToBeOccupied() {
            if (occupied) {
                throw new InvalidOccupyInfoException();
            }

            occupied = true;
        }
    }
}
