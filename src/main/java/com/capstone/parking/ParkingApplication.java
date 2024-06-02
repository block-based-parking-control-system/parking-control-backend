package com.capstone.parking;

import com.capstone.parking.config.rosbridge.RosBridgeClient;
import com.capstone.parking.entity.parkinglot.ParkingLotEn;
import com.capstone.parking.entity.parkinglot.ParkingStatus;
import com.capstone.parking.repository.ParkingLotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ParkingApplication implements CommandLineRunner {

    private final ParkingLotRepository parkingLotRepository;
    private final RosBridgeClient rosBridgeClient;

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

    @Override
    public void run(String... args) throws Exception {
        boolean isConnected = rosBridgeClient.connectBlocking();

        log.info("ROS Bridge connection result: {}", isConnected);


        // 예제: 토픽 구독
        //rosBridgeClient.subscribe("/example_topic");

        // 예제: 토픽 발행
        //rosBridgeClient.publish("/example_topic", "Hello ROS!");

    }

}
