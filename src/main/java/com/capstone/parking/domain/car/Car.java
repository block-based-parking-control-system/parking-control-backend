package com.capstone.parking.domain;

import com.capstone.parking.domain.exception.InvalidCarStatusException;
import lombok.Getter;

import java.awt.Point;
import java.util.List;

@Getter
public class Car {

    private CarStatus status; //차량의 현재 상태 (입차 전, 입차 중, 입차 완료, 출차 전, 출차 중, 출차 완료)

    private MovingInfo movingInfo; //이동 관련 정보(이동 경로, 예상 소요 시간, 현재 위치 등)

    public Car(CarStatus status) {
        this.status = status;
    }

    /**
     * 차량 입차 시도
     * @param route 관제 시스템으로부터 제공받은 이동경로
     * @return 제공받은 이동 경로, 예상 소요 시간, 현재 위치를 포함하는 이동 정보
     */
    public MovingInfo startEntering(List<Point> route) {
        if (status != CarStatus.BEFORE_ENTER) {
            throw new InvalidCarStatusException();
        }

        status = CarStatus.ENTERING;

        movingInfo = new MovingInfo(route);
        return movingInfo;
    }

    /**
     * 차량 주차(입차) 완료
     * @return 주차한 위치
     */
    public Point completeEntering() {
        if (status != CarStatus.ENTERING) {
            throw new InvalidCarStatusException();
        }

        status = CarStatus.PARKED;

        return movingInfo.getCurrentLocation().getPoint();
    }

    /**
     * 차량 출차 시도
     * @param route 관제 시스템으로부터 제공받은 이동경로
     * @return 제공받은 이동 경로, 예상 소요 시간, 현재 위치를 포함하는 이동 정보
     */
    public MovingInfo startDeparture(List<Point> route) {
        if (status != CarStatus.PARKED) {
            throw new InvalidCarStatusException();
        }

        status = CarStatus.DEPARTING;

        movingInfo = new MovingInfo(route);
        return movingInfo;
    }

    /**
     * 차량 출차 완료
     */
    public void completeDeparture() {
        if (status != CarStatus.DEPARTING) {
            throw new InvalidCarStatusException();
        }

        status = CarStatus.DEPARTURE_COMPLETE;
    }


}
