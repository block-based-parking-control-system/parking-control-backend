package com.capstone.parking.repository;

import com.capstone.parking.entity.car.CarEn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarEn, Long> {
}
