package com.capstone.parking.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.awt.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Location {

    private List<Point> location;

    public static Location of(List<Point> location) {
        return new Location(location);
    }

}
