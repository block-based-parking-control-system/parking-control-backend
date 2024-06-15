package com.capstone.parking.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Completion {

    private boolean success;

    public static Completion success() {
        return new Completion(true);
    }

}
