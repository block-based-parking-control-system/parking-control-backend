package com.capstone.parking.controller;

import com.capstone.parking.config.rosbridge.RosBridgeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ros")
@RequiredArgsConstructor
@Slf4j
public class RosController {

    private final RosBridgeClient rosBridgeClient;

    @PostMapping("/publish")
    public void publish(@RequestParam("topic") String topic, @RequestParam("message") String message) {
        if (rosBridgeClient.isClosed()) {
            rosBridgeClient.reconnect();
        }
        log.info("ready state : {}", rosBridgeClient.getReadyState());
        rosBridgeClient.publish(topic, message);
    }

    @GetMapping("/subscribe")
    public void subscribe(@RequestParam("topic") String topic) {
        if (rosBridgeClient.isClosed()) {
            rosBridgeClient.reconnect();
        }
        log.info("ready state : {}", rosBridgeClient.getReadyState());
        rosBridgeClient.subscribe(null, topic);
    }
}

