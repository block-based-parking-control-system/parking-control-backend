package com.capstone.parking.util.temp;

import com.capstone.parking.service.dto.EntranceRouteInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * TODO 변경 예정
 * 차량과의 통신을 구축하기 전에 테스트 용으로 임의의 이동 경로를 생성
 */
public class RandomRouteGenerator {

    public static EntranceRouteInfo generateEntranceRoute() {
        List<Point> route = generateRandomPointList();
        Long parkingLotNum = new Random().nextLong(1, 101); //1 ~ 100 사이 정수

        return new EntranceRouteInfo(route, parkingLotNum);
    }

    public static List<Point> generateExitRoute() {
        return generateRandomPointList();
    }

    public static List<Point> generateRandomPointList() {
        Random random = new Random();

        List<Point> pointList = new ArrayList<>();

        int numberOfPoints = random.nextInt(6) + 5;

        for (int i = 0; i < numberOfPoints; i++) {
            int x = random.nextInt(51);
            int y = random.nextInt(51);
            Point point = new Point(x, y);
            pointList.add(point);
        }

        return pointList;
    }

    public static Point generateRandomSinglePoint() {
        Random random = new Random();

        int x = random.nextInt(51);
        int y = random.nextInt(51);

        return new Point(x, y);
    }

    public static List<Point> generateRandomDoublePoint() {
        Random random = new Random();

        List<Point> pointList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int x = random.nextInt(51);
            int y = random.nextInt(51);

            pointList.add(new Point(x, y));
        }

        return pointList.stream().toList();
    }
}
