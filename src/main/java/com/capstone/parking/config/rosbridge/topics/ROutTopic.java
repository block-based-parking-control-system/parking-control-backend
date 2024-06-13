package com.capstone.parking.config.rosbridge.topics;

import com.capstone.parking.controller.response.dto.EntranceInit;
import com.capstone.parking.controller.response.dto.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Slf4j
public class ROutTopic {

    private static final List<Point> entranceRoute = EntranceInit.create().getEntranceRoute();

    private String op;

    private String topic;

    private ROutMessage msg;

    public static ROutTopic parse(String message) {
        try {
            ROutTopic rOutTopic = new ObjectMapper().readValue(message, ROutTopic.class);
            log.info("[ROutTopic.parse()] [SUCCESS] rOutTopic: {}", rOutTopic);
            return rOutTopic;
        } catch (JsonProcessingException e) {
            log.error("[ROutTopic.parse()] [FAIL] Fail JSON Parsing", e);
            return null;
        }
    }

    public Location toLocation() {
        double positionX = this.getMsg().getPosition().getX();
        double positionY = this.getMsg().getPosition().getY();

        int occupiedType = getOccupiedType(positionX, positionY);

        int y = (115 - (int) positionX) / 10 + 1;
        int x = (int) ((-1) * positionY) / 10;

        List<Point> location = new ArrayList<>();
        Point current = new Point(x, y);
        location.add(current);

        int idx = entranceRoute.indexOf(current);
        if (occupiedType == 1) { //2칸 점유 (현재칸, 앞칸)
            if (idx != entranceRoute.size() - 1) {
                location.add(entranceRoute.get(idx + 1));
            }
        } else if (occupiedType == 2) { //2칸 점유 (현재칸, 뒷칸)
            if (idx != 0) {
                location.add(entranceRoute.get(idx - 1));
            }
        }

        return Location.of(location);
    }

    private int getOccupiedType(double positionX, double positionY) {
        double digitY = ((-1) * positionY) % 10;

        if (digitY >= 0 && digitY < 2.5) {
            log.info("[ROutTopic.toLocation()] occupiedType: {}, 앞칸 + 현재칸, x", 1);
            return 1; //2칸 점유(현재칸, 앞칸)
        }
        if (digitY>= 7.5) {
            log.info("[ROutTopic.toLocation()] occupiedType: {}, 뒷칸 + 현재칸, x", 2);
            return 2; //2칸 점유(현재칸, 뒷칸)
        }

        double digitX = positionX % 10;

        if (digitX >= 2.5 && digitX < 5) {
            log.info("[ROutTopic.toLocation()] occupiedType: {}, 앞칸 + 현재칸, y", 1);
            return 1; //2칸 점유(현재칸, 앞칸)
        }
        if (digitX >= 5 && digitX < 7.5) {
            log.info("[ROutTopic.toLocation()] occupiedType: {}, 뒷칸 + 현재칸, y", 2);
            return 2; //2칸 점유(현재칸, 뒷칸)
        }

        log.info("[ROutTopic.toLocation()] occupiedType: {}, 현재칸", 0);
        return 0; //1칸 점유(현재칸)
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    static class ROutMessage {
        private Position position;

        private Orientation orientation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    static class Position {
        private double x;
        private double y;
        private double z;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    static class Orientation {
        private double x;
        private double y;
        private double z;
        private double w;
    }
}
