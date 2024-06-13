package com.capstone.parking.controller;

import com.capstone.parking.config.rosbridge.RosBridgeClient;
import com.capstone.parking.controller.response.dto.EntranceInit;
import com.capstone.parking.controller.response.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static com.capstone.parking.controller.response.dto.ResponseType.ENTRANCE_ROUTE;

@RestController
@RequestMapping("/api/car/entrance")
@RequiredArgsConstructor
@Slf4j
public class CarEntranceController {

    private final RosBridgeClient rosBridgeClient;

    @GetMapping
    public SseEmitter entrance() {
        if (rosBridgeClient.isClosed()) {
            rosBridgeClient.reconnect();
            log.info("[GET /api/car/entrance] reconnected to ROS Bridge");
        }

        SseEmitter emitter = new SseEmitter();
        sendMessage(emitter, ResponseDto.of(ENTRANCE_ROUTE, EntranceInit.create()));

        rosBridgeClient.subscribe(emitter, "/Rout");

        return emitter;
    }

    private static void sendMessage(SseEmitter emitter, ResponseDto<?> responseDto) {
        try {
            emitter.send(
                    SseEmitter.event()
                            .data(responseDto)
            );
        } catch (IOException e) {
            log.error("[CarEntranceController] Error occurred while sending message", e);
            emitter.completeWithError(e);
        }
    }

}
