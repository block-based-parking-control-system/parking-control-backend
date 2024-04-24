package com.capstone.parking.service;

import com.capstone.parking.entity.car.CarEn;
import com.capstone.parking.entity.parkinglot.ParkingLotEn;
import com.capstone.parking.entity.parkinglot.ParkingStatus;
import com.capstone.parking.repository.CarRepository;
import com.capstone.parking.repository.ParkingLotRepository;
import com.capstone.parking.service.dto.EntranceRouteInfo;
import com.capstone.parking.util.temp.RandomRouteGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;

import static com.capstone.parking.domain.car.CarStatus.BEFORE_ENTER;
import static com.capstone.parking.domain.car.CarStatus.PARKED;

@Service
@RequiredArgsConstructor
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final ParkingLotRepository parkingLotRepository;

    public EntranceRouteInfo loadEntranceRoute() {
        EntranceRouteInfo routeInfo = RandomRouteGenerator.generateEntranceRoute(); //TODO 챠량과의 통신을 구축하기 전에 임시로 작성한 코드

        ParkingLotEn parkingLot = parkingLotRepository.findById(routeInfo.getParkingLotNum()).orElseThrow();
        if (parkingLot.isOccupied()) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }

        CarEn savedCar = carRepository.save(new CarEn(BEFORE_ENTER, parkingLot));
        savedCar.readyToPark(parkingLot);

        return routeInfo;
    }

    public List<Point> loadExitRoute(Long carId) {
        CarEn foundCar = carRepository.findById(carId).orElseThrow();
        if (!foundCar.isParked()) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }

        List<Point> route = RandomRouteGenerator.generateExitRoute(); //TODO 챠량과의 통신을 구축하기 전에 임시로 작성한 코드
        foundCar.readyToExit();

        return route;
    }
}
