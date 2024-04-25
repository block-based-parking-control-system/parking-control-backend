package com.capstone.parking;

import com.capstone.parking.entity.parkinglot.ParkingLotEn;
import com.capstone.parking.entity.parkinglot.ParkingStatus;
import com.capstone.parking.repository.ParkingLotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class ParkingApplication {

    private final ParkingLotRepository parkingLotRepository;

    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }

    /**
     * TODO 임시 코드
     */
    @PostConstruct
    public void init() {
        List<ParkingLotEn> tobeSaved = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            tobeSaved.add(new ParkingLotEn((long) i, ParkingStatus.NON_OCCUPIED));
        }

        parkingLotRepository.saveAll(tobeSaved);
    }

}
