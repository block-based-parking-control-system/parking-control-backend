package com.capstone.parking;

import com.capstone.parking.config.rosbridge.RosBridgeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ParkingApplication implements CommandLineRunner {

    private final RosBridgeClient rosBridgeClient;

    public static void main(String[] args) {
        SpringApplication.run(ParkingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        boolean isConnected = rosBridgeClient.connectBlocking();
        log.info("ROS Bridge connection result: {}", isConnected);
    }

}
