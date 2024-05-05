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

    /**
     * 1. api/v2/car/entrance 로 요청이 오면 SSE 연결을 맺음
     * 2. 첫번째 반환값: EntranceRouteInfo 인스턴스 (이동 경로 + 주차할 위치)
     * 3. 두번째 ~ n번째 반환값: 차량의 현재 위치
     *      3-1. 1초마다 한번씩 보냄
     *      3-2. 차량이 점유하고 있는 칸의 정보를 모두 반환 (한 칸 or 두 칸)
     * 4. 3번 도중 장애물에 의해 이동 경로가 갱신되면(이벤트 발생 (1초마다 검사하는 게 X)), 갱신된 EntranceRouteInfo(갱신된 이동 경로 + 갱신된 주차할 위치) 를 반환
     *      4-1. 이후 3번 과정 반복
     * 5. 마지막 반환값: 정상적으로 주차됐는지 여부
     *
     * 기본적으로는 1초에 한 번씩 현재 위치를 스트리밍함
     * 그러나 이동 경로가 갱신되면 위의 규칙을 무시하고 그 즉시 새로운 EntranceRoute 데이터를 스트리밍 해야함
     * 이후로는 다시 1초에 한 번씩 현재 위치 스트리밍
     * TODO 비동기를 써야 하는지
     */
    @GetMapping(value = "/entrance", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux> enter() {

        Flux<ResponseApiV2> dataStream = Mono.just(
                new ResponseApiV2<>(ResponseType.ENTRANCE_ROUTE, carService.loadEntranceRoute())
                ).flux()
                .concatWith(
                        Flux.interval(Duration.ZERO, Duration.ofSeconds(1))
                                .map(sequence ->
                                        new ResponseApiV2(ResponseType.CURRENT_LOCATION_SINGLE, new Point((int) Math.random(), (int) Math.random()))
                                )
                );

        return ResponseEntity.ok().body(dataStream);
    }

    /**
     * 1. api/v2/car/exit 로 요청이 오면 SSE 연결을 맺음
     * 2. 첫번째 반환값: List<Point> (이동 경로)
     * 3. 두번째 ~ n번째 반환값: 차량의 현재 위치
     *      3-1. 1초마다 한번씩 보냄
     *      3-2. 차량이 점유하고 있는 칸의 정보를 모두 반환 (한 칸 or 두 칸)
     * 4. 3번 도중 장애물에 의해 이동 경로가 갱신되면(이벤트 발생 (1초마다 검사하는 게 X)), 갱신된 List<Point> (갱신된 이동 경로) 를 반환
     *      4-1. 이후 3번 과정 반복
     * 5. 마지막 반환값: 정상적으로 출차됐는지 여부
     *
     * 기본적으로는 1초에 한 번씩 현재 위치를 스트리밍함
     * 그러나 이동 경로가 갱신되면 1초의 텀을 무시하고 그 즉시 새로운 EntranceRoute 데이터를 스트리밍 해야함
     * 이후로는 다시 1초에 한 번씩 현재 위치 스트리밍
     * TODO 비동기를 써야 하는지
     */
    //TODO carId 파라미터를 받지 말고 인증 처리를 해서 세션이나 토큰 값으로 Car 엔티티를 찾기
    @GetMapping("/exit")
    public ResponseEntity<ResponseApiV1> exit(@RequestParam("id") Long carId) {
        List<Point> route = carService.loadExitRoute(carId);

        //TODO SSE 이용
        return new ResponseEntity<>(new ResponseApiV1(true, route), HttpStatus.OK);
    }
}
