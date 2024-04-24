package com.capstone.parking.repository;

import com.capstone.parking.entity.parkinglot.ParkingLotEn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotEn, Long> {
}
