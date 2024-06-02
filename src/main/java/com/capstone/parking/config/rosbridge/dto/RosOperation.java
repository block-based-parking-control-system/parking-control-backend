package com.capstone.parking.config.rosbridge.dto;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RosOperation {

    String toJson() throws JsonProcessingException;

}
