package com.capstone.parking.controller;

import com.capstone.parking.controller.response.ResponseApiV3;
import com.capstone.parking.controller.response.ResponseType;
import com.capstone.parking.domain.car.MovingInfo;
import com.capstone.parking.service.CarService;
import com.capstone.parking.service.dto.EntranceRouteInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v3/car")
@RequiredArgsConstructor
@Slf4j
public class CarControllerV3 {

    private final CarService carService;
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @GetMapping(value = "/example", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> example() throws IOException {
        Stream<String> lines = Files.lines(Path.of("C:\\projects\\parking\\build.gradle"));
        AtomicInteger counter = new AtomicInteger(1);

        return Flux.fromStream(lines)
                .filter(line -> !line.isBlank())
                .map(line -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(counter.getAndIncrement()))
                        .data(line)
                        .event("lineEvent")
                        .retry(Duration.ofMillis(1000))
                        .build())
                .delayElements(Duration.ofMillis(500));
    }

    /**
     * 1. api/v2/car/entrance 로 요청이 오면 SSE 연결을 맺음
     * 2. 첫번째 반환값: EntranceRouteInfo 인스턴스 (이동 경로 + 주차할 위치)
     * 3. 두번째 ~ n번째 반환값: 차량의 현재 위치
     * 3-1. 1초마다 한번씩 보냄
     * 3-2. 차량이 점유하고 있는 칸의 정보를 모두 반환 (한 칸 or 두 칸)
     * 4. 3번 도중 장애물에 의해 이동 경로가 갱신되면(이벤트 발생 (1초마다 검사하는 게 X)), 갱신된 EntranceRouteInfo(갱신된 이동 경로 + 갱신된 주차할 위치) 를 반환
     * 4-1. 이후 3번 과정 반복
     * 5. 마지막 반환값: 정상적으로 주차됐는지 여부
     * <p>
     * 기본적으로는 1초에 한 번씩 현재 위치를 스트리밍함
     * 그러나 이동 경로가 갱신되면 위의 규칙을 무시하고 그 즉시 새로운 EntranceRoute 데이터를 스트리밍 해야함
     * 이후로는 다시 1초에 한 번씩 현재 위치 스트리밍
     **/
    @GetMapping(value = "/entrance", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ResponseApiV3<?>>> enterV3() {
        AtomicInteger counter = new AtomicInteger(1); //sse 스트리밍 카운터

        EntranceRouteInfo routeInfo = carService.loadEntranceRoute();
        ResponseApiV3<EntranceRouteInfo> initialEntranceRoute
                = ResponseApiV3.of(true, ResponseType.ENTRANCE_ROUTE, routeInfo);

        Flux<ServerSentEvent<ResponseApiV3<?>>> result = Flux.just(
                buildSse(counter, initialEntranceRoute)
        );

        MovingInfo movingInfo = new MovingInfo(routeInfo.getRoute());
        result = result.concatWith(
                Flux.interval(Duration.ofMillis(1000))
                        .map(tick -> movingInfo.getCurrentLocation())
                        .map(pointList -> {
                                    if (pointList.size() == 1) return buildSse(counter, ResponseApiV3.of(true, ResponseType.CURRENT_SINGLE_LOCATION, pointList));
                                    else return buildSse(counter, ResponseApiV3.of(true, ResponseType.CURRENT_DOUBLE_LOCATION, pointList));
                                }
                        )
                        .takeUntil(
                                serverSentEvent -> Integer.parseInt(serverSentEvent.id()) > 4 //TODO 차량과 통신을 시작하면, 이 부분을 '새로운 경로를 받았을 경우' 로 수정
                        )
        );

        /*
        TODO 차량과 통신을 시작하면, 아래 코드는 새로운 경로를 받는 이벤트가 발생한 경우에만 수행
            위의 result.mergeWith(...) 코드와 묶어서 반복문 형태로 만들면 되지 않을까 싶음
         */
        EntranceRouteInfo updatedRouteInfo = carService.loadEntranceRoute();
        ResponseApiV3<EntranceRouteInfo> initialEntranceRoute2
                = ResponseApiV3.of(true, ResponseType.ENTRANCE_ROUTE, updatedRouteInfo);

        result = result.concatWith(
                Flux.just(buildSse(counter, initialEntranceRoute2))
        );

        MovingInfo updatedmovingInfo = new MovingInfo(updatedRouteInfo.getRoute());
        result = result.concatWith(
                Flux.interval(Duration.ofMillis(1000))
                        .takeWhile(tick -> updatedmovingInfo.getCurrentIndexes() != null)
                        .map(tick -> updatedmovingInfo.getCurrentLocation())
                        .map(pointList -> {
                                    if (pointList.size() == 1) return buildSse(counter, ResponseApiV3.of(true, ResponseType.CURRENT_SINGLE_LOCATION, pointList));
                                    else return buildSse(counter, ResponseApiV3.of(true, ResponseType.CURRENT_DOUBLE_LOCATION, pointList));
                                }
                        )
        );

        return result;
    }

    private static ServerSentEvent<ResponseApiV3<?>> buildSse(AtomicInteger counter, ResponseApiV3<?> responseApi) {
        return ServerSentEvent.<ResponseApiV3<?>>builder()
                .id(String.valueOf(counter.getAndIncrement()))
                .data(responseApi)
                .event(responseApi.getType().getDescription())
                .retry(Duration.ofMillis(1000))
                .build();
    }

    /**
     * 1. api/v2/car/exit 로 요청이 오면 SSE 연결을 맺음
     * 2. 첫번째 반환값: List<Point> (이동 경로)
     * 3. 두번째 ~ n번째 반환값: 차량의 현재 위치
     * 3-1. 1초마다 한번씩 보냄
     * 3-2. 차량이 점유하고 있는 칸의 정보를 모두 반환 (한 칸 or 두 칸)
     * 4. 3번 도중 장애물에 의해 이동 경로가 갱신되면(이벤트 발생 (1초마다 검사하는 게 X)), 갱신된 List<Point> (갱신된 이동 경로) 를 반환
     * 4-1. 이후 3번 과정 반복
     * 5. 마지막 반환값: 정상적으로 출차됐는지 여부
     * <p>
     * 기본적으로는 1초에 한 번씩 현재 위치를 스트리밍함
     * 그러나 이동 경로가 갱신되면 1초의 텀을 무시하고 그 즉시 새로운 EntranceRoute 데이터를 스트리밍 해야함
     * 이후로는 다시 1초에 한 번씩 현재 위치 스트리밍
     * TODO 비동기를 써야 하는지
     */
    //TODO carId 파라미터를 받지 말고 인증 처리를 해서 세션이나 토큰 값으로 Car 엔티티를 찾기
    @GetMapping(value = "/exit", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> exit(@RequestParam("id") Long carId) {
        return ResponseEntity.ok().body(null);
    }
}
