package com.capstone.parking.domain.car;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class MovingInfo {

    private List<Point> route; //이동 경로

    private List<Integer> currentIndexes; //현재 위치에 해당하는 route 의 인덱스 (최대 2칸 점유 가능)

    private int nextIndex;

    public MovingInfo(List<Point> route) {
        this.route = route;
        this.currentIndexes = new ArrayList<>();

        nextIndex = 0;
    }

    public List<Integer> getCurrentIndexes() {
        if (currentIndexes.size() == 1 && nextIndex >= route.size()) {
            return null;
        }

        return currentIndexes;
    }

    public List<Point> getCurrentLocation() {
        if (!moveForward()) {
            return null;
        }

        List<Point> result = changeToPointList();
        return result;
    }

    private boolean moveForward() {
        if (nextIndex >= route.size()) {
            return false;
        }

        if (currentIndexes.size() == 2) { //현재 두 칸 점유
            currentIndexes.remove(0);
            nextIndex++;
        } else if (currentIndexes.size() == 1){ //현재 한 칸 점유
            currentIndexes.add(nextIndex);
        } else { //최초 이동
            currentIndexes.add(nextIndex++);
        }
        return true;
    }

    private List<Point> changeToPointList() {
        List<Point> result = new ArrayList<>();

        for (Integer i : currentIndexes) {
            result.add(route.get(i));
        }

        return result;
    }
}
