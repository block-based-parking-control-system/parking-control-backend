package com.capstone.parking.controller;

import com.capstone.parking.config.websocket.RosBridgeClient;
import com.capstone.parking.controller.response.ResponseApi;
import com.capstone.parking.service.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

import static com.capstone.parking.controller.response.ResponseType.ENTRANCE_ROUTE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/api/car/entrance")
@RequiredArgsConstructor
@Slf4j
public class CarEntranceController {

    private final RosBridgeClient rosBridgeClient;
    private final CarService carService;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ResponseApi<?>>> entrance() {
        if (rosBridgeClient.isClosed()) {
            rosBridgeClient.reconnect();
            log.info("[GET /api/car/entrance] reconnected to ROS Bridge");
        }

        AtomicLong counter = new AtomicLong(1L);

        String topic = "/waypoints";
        rosBridgeClient.subscribe(topic);

        return null;
        /*
        return Flux.create(sink -> {
            rosBridgeClient.addMessageHandler(topic, message -> {
                ResponseApi<String> responseApi = ResponseApi.of(true, ENTRANCE_ROUTE, message);
                ServerSentEvent<ResponseApi<?>> event = ServerSentEvent.<ResponseApi<?>>builder()
                        .id(String.valueOf(counter.getAndIncrement()))
                        .event("entrance_route")
                        .data(responseApi)
                        .build();
                sink.next(event);
            });
        });
        */
    }

}
