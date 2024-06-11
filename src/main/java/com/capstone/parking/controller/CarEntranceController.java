package com.capstone.parking.controller;

import com.capstone.parking.config.rosbridge.RosBridgeClient;
import com.capstone.parking.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/car/entrance")
@RequiredArgsConstructor
@Slf4j
public class CarEntranceController {

    private final RosBridgeClient rosBridgeClient;
    private final CarService carService;

    @GetMapping//(produces = TEXT_EVENT_STREAM_VALUE)
    public SseEmitter entrance() {
        if (rosBridgeClient.isClosed()) {
            rosBridgeClient.reconnect();
            log.info("[GET /api/car/entrance] reconnected to ROS Bridge");
        }
         //entrance_start 토픽으로 start 메시지 발행 -> ros bridge가
        rosBridgeClient.publish("/entrance_start", "start");

        SseEmitter emitter = new SseEmitter();

        /*
        TODO: 처음 통신을 시작하는 방법
          1. ROS Bridge 서버에서 특정 토픽 구독 (ROS Bridge 서버 구동 시 자동으로 구독하도록 코딩)
          2. 스프링부트 서버(ROS Bridge 클라이언트)에서 이 특정 토픽으로 메시지 발행
          3. 메시지를 받은 ROS Bridge 서버는 차량의 이동 경로를 계산해서 스프링부트 서버로 발행
         */

        rosBridgeClient.subscribe(emitter, "/waypoints", "/current_point"); //TODO 토픽 이름은 추후 변경
        return emitter;
    }

}
