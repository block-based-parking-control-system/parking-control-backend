package com.capstone.parking.entity.car;

import com.capstone.parking.domain.car.CarStatus;
import com.capstone.parking.entity.parkinglot.ParkingLot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Car {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_status", nullable = false)
    private CarStatus status; //차량의 상태

    @OneToOne
    @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot; //차량의 주차 공간 (status 가 입차 중, 입차 완료일 때에만 값이 존재)

    public Car(CarStatus status) {
        this.status = status;
    }

    public void readyToPark(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }
}
