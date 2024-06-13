package com.capstone.parking.config.rosbridge.topics;

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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Slf4j
public class ROutTopic {

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
        int y = (115 - (int) this.getMsg().getPosition().getX()) / 10 + 1;
        int x = (int) ((-1) * this.getMsg().getPosition().getY()) / 10;

        return Location.of(
                List.of(new Point(x, y))
        );
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
