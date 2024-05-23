package com.capstone.parking.controller;

import com.capstone.parking.controller.response.ResponseApi;
import com.capstone.parking.service.CarService;
import com.capstone.parking.service.dto.EntranceRouteInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.capstone.parking.controller.response.ResponseType.CURRENT_SINGLE_LOCATION;
import static com.capstone.parking.controller.response.ResponseType.ENTRANCE_ROUTE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/api/steps/car")
@RequiredArgsConstructor
@Slf4j
public class CarStepController {

    private final CarService carService;

    /**
     * 1. api/steps/car/entrance/v1 으로 요청이 오면 SSE 연결을 맺음
     * 2. EntranceRouteInfo 인스턴스 반환
     */
    @GetMapping(value = "/entrance/v1", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ResponseApi<?>>> entranceV1() {
        AtomicInteger counter = new AtomicInteger(1);

        EntranceRouteInfo initialRouteInfo = carService.loadEntranceRoute();
        ResponseApi<EntranceRouteInfo> initialRouteInfoResponse = ResponseApi.of(true, ENTRANCE_ROUTE, initialRouteInfo);

        return Flux.just(
                buildSse(counter, initialRouteInfoResponse)
        );
    }

    @GetMapping(value = "/entrance/v2", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ResponseApi<?>>> entranceV2() {
        AtomicInteger counter = new AtomicInteger(1);

        EntranceRouteInfo initialRouteInfo = carService.loadEntranceRoute();
        ResponseApi<EntranceRouteInfo> initialRouteInfoResponse = ResponseApi.of(true, ENTRANCE_ROUTE, initialRouteInfo);

        return Flux.concat(
                Flux.just(buildSse(counter, initialRouteInfoResponse)),
                Flux.interval(Duration.ofSeconds(1))
                        .zipWithIterable(initialRouteInfo.getRoute())
                        .map(tuple -> {
                            Point point = tuple.getT2();
                            ResponseApi<Point> pointResponse = ResponseApi.of(true, CURRENT_SINGLE_LOCATION, point);
                            return buildSse(counter, pointResponse);
                        })
        );
    }

    @GetMapping(value = "/entrance/v3", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ResponseApi<?>>> entranceV3() {
        AtomicInteger counter = new AtomicInteger(1); //sse 스트리밍 카운터
        AtomicBoolean streamCompleted = new AtomicBoolean(true); //스트리밍 완료 여부

        EntranceRouteInfo initialRouteInfo = carService.loadEntranceRoute(); //초기 이동 경로 정보
        ResponseApi<EntranceRouteInfo> initialRouteInfoResponse = ResponseApi.of(true, ENTRANCE_ROUTE, initialRouteInfo);

        Flux<ServerSentEvent<ResponseApi<?>>> initialStream = Flux.concat(
                        Flux.just(buildSse(counter, initialRouteInfoResponse)),
                        Flux.interval(Duration.ofSeconds(1))
                                .zipWithIterable(initialRouteInfo.getRoute())
                                .map(tuple -> {
                                    Point point = tuple.getT2();
                                    ResponseApi<Point> pointResponse = ResponseApi.of(true, CURRENT_SINGLE_LOCATION, point);
                                    return buildSse(counter, pointResponse);
                                })
                )
                .takeUntil(shouldTakeUpdatedRoute(streamCompleted));

        Flux<ServerSentEvent<ResponseApi<?>>> updatedStream = Flux.defer(() -> {
            if (streamCompleted.get()) {
                return Flux.empty(); // initialStream 이 완료되면 더 이상 스트림을 생성하지 않음
            }

            EntranceRouteInfo updatedRouteInfo = carService.loadEntranceRoute();
            ResponseApi<EntranceRouteInfo> updatedRouteInfoResponse = ResponseApi.of(true, ENTRANCE_ROUTE, updatedRouteInfo);

            return Flux.concat(
                            Flux.just(buildSse(counter, updatedRouteInfoResponse)),
                            Flux.interval(Duration.ofSeconds(1))
                                    .zipWithIterable(updatedRouteInfo.getRoute())
                                    .map(tuple -> {
                                        Point point = tuple.getT2();
                                        ResponseApi<Point> pointResponse = ResponseApi.of(true, CURRENT_SINGLE_LOCATION, point);
                                        return buildSse(counter, pointResponse);
                                    })
                    )
                    .takeUntil(x -> { //TODO takeUntil() 의 파라미터로 특정 이벤트 발생 여부 체크
                        streamCompleted.set(true);
                        return false;
                    });
        });

        return initialStream
                .concatWith(
                        updatedStream.repeat(() -> !streamCompleted.get())
                );
    }

    private static Predicate<ServerSentEvent<ResponseApi<?>>> shouldTakeUpdatedRoute(AtomicBoolean streamCompleted) {
        return x -> { //TODO takeUntil() 의 파라미터로 특정 이벤트 발생 여부 체크
            boolean shouldComplete = x.id().equals("3");
            if (shouldComplete) {
                streamCompleted.set(false);
                log.info("initialStream completed because id is equal to 3, streamCompleted: {}", streamCompleted.get());
            }
            return shouldComplete;
        };
    }

    private static ServerSentEvent<ResponseApi<?>> buildSse(AtomicInteger counter, ResponseApi<?> responseApi) {
        return ServerSentEvent.<ResponseApi<?>>builder()
                .id(String.valueOf(counter.getAndIncrement()))
                .data(responseApi)
                .event(responseApi.getType().getDescription())
                .retry(Duration.ofMillis(1000))
                .build();
    }


}
