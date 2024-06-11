package com.capstone.parking.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseDto<T> {

    private int type;

    private T data;

    public static <T> ResponseDto<T> of(ResponseType type, T data) {
        return new ResponseDto<>(type.getValue(), data);
    }
}
