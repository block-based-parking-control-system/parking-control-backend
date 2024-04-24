package com.capstone.parking.controller;

import com.capstone.parking.controller.response.ResponseApi;
import com.capstone.parking.service.CarService;
import com.capstone.parking.service.dto.EntranceRouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/entrance")
    public ResponseEntity<ResponseApi> enter() {
        EntranceRouteInfo routeInfo = carService.loadEntranceRoute();

        //TODO 웹소켓 이용

        return new ResponseEntity<>(new ResponseApi(true, routeInfo), HttpStatus.OK);
    }

    //TODO carId 파라미터를 받지 말고 인증 처리를 해서 세션이나 토큰 값으로 Car 엔티티를 찾기
    @GetMapping("/exit")
    public ResponseEntity<ResponseApi> exit(@RequestParam("id") Long carId) {
        List<Point> route = carService.loadExitRoute(carId);

        //TODO 웹소켓 이용
        return new ResponseEntity<>(new ResponseApi(true, route), HttpStatus.OK);
    }

}
