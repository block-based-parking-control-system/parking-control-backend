package com.capstone.parking.config.rosbridge.dto.impl;

import com.capstone.parking.config.rosbridge.dto.RosOperation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import static com.capstone.parking.config.rosbridge.dto.OperationType.SUBSCRIBE;

@Getter
public class RosSubscribe implements RosOperation {

    private final String op = SUBSCRIBE.name().toLowerCase();

    private final String topic;

    public static RosSubscribe of(String topic) {
        return new RosSubscribe(topic);
    }

    private RosSubscribe(String topic) {
        this.topic = topic;
    }

    @Override
    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("[RosSubscribe.toJson()] Json 포매팅 실패", e);
        }
    }
}
