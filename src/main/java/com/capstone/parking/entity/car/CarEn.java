package com.capstone.parking.entity.car;

import com.capstone.parking.domain.car.CarStatus;
import com.capstone.parking.entity.parkinglot.ParkingLotEn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "car")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CarEn {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_status", nullable = false)
    private CarStatus status; //차량의 상태

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id")
    private ParkingLotEn parkingLot; //차량의 주차 공간 (status 가 입차 중, 입차 완료일 때에만 값이 존재)

    public CarEn(CarStatus status, ParkingLotEn parkingLot) {
        this.status = status;
        this.parkingLot = parkingLot;
    }

    public boolean isParked() {
        return status == CarStatus.PARKED;
    }

    public void readyToPark(ParkingLotEn parkingLot) {
        if (this.status != CarStatus.BEFORE_ENTER) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }

        this.status = CarStatus.ENTERING;
        this.parkingLot = parkingLot;
        this.parkingLot.changeToBeOccupied();
    }

    public void readyToExit() {
        if (this.status != CarStatus.PARKED) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }

        this.status = CarStatus.DEPARTING;
        this.parkingLot.changeNonOccupied();
        this.parkingLot = null;
    }
}
