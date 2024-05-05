package com.capstone.parking.controller;

import com.capstone.parking.controller.response.ResponseApiV1;
import com.capstone.parking.controller.response.ResponseApiV2;
import com.capstone.parking.controller.response.ResponseType;
import com.capstone.parking.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v2/car")
@RequiredArgsConstructor
public class CarControllerV2 {

    private final CarService carService;

    @GetMapping(value = "/entrance", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux> enter() {

        Flux<ResponseApiV2> dataStream = Mono.just(
                new ResponseApiV2<>(ResponseType.ENTRANCE_ROUTE, carService.loadEntranceRoute())
                ).flux()
                .concatWith(
                        Flux.interval(Duration.ZERO, Duration.ofSeconds(1))
                                .map(sequence ->
                                        new ResponseApiV2(ResponseType.CURRENT_SINGLE_LOCATION, new Point((int) Math.random(), (int) Math.random()))
                                )
                );

        return ResponseEntity.ok().body(dataStream);
    }

    @GetMapping("/exit")
    public ResponseEntity<ResponseApiV1> exit(@RequestParam("id") Long carId) {
        List<Point> route = carService.loadExitRoute(carId);

        //TODO SSE 이용
        return new ResponseEntity<>(new ResponseApiV1(true, route), HttpStatus.OK);
    }
}
