package com.capstone.parking.entity.parkinglot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "parking_lot")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ParkingLotEn {

    @Id
    @Column(name = "parking_lot_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "parking_lot_status", nullable = false)
    private ParkingStatus status;

    public ParkingLotEn(Long id, ParkingStatus status) {
        this.id = id;
        this.status = status;
    }

    public boolean isOccupied() {
        return (status == ParkingStatus.OCCUPIED
                || status == ParkingStatus.TO_BE_OCCUPIED);
    }

    public void changeToBeOccupied() {
        if (status != ParkingStatus.NON_OCCUPIED) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }
        status = ParkingStatus.TO_BE_OCCUPIED;
    }

    public void changeNonOccupied() {
        if (status != ParkingStatus.OCCUPIED) {
            throw new IllegalStateException(); //TODO 예외 구체화
        }
        status = ParkingStatus.NON_OCCUPIED;
    }
}
