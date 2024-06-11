package com.capstone.parking.config.rosbridge.dto.impl;

import com.capstone.parking.config.rosbridge.dto.RosOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.capstone.parking.config.rosbridge.dto.OperationType.PUBLISH;

@Getter
public class RosPublish implements RosOperation {

    private final String op = PUBLISH.name().toLowerCase();

    private final String topic;

    private final RosMessage msg;

    public static RosPublish of(String topic, String data) {
        return new RosPublish(topic, RosMessage.of(data));
    }

    private RosPublish(String topic, RosMessage msg) {
        this.topic = topic;
        this.msg = msg;
    }

    @Override
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("[RosPublish.toJson()] Json 포매팅 실패", e);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    static class RosMessage {

        private final String data;

        public static RosMessage of(String data) {
            return new RosMessage("start");
        }
    }
}
