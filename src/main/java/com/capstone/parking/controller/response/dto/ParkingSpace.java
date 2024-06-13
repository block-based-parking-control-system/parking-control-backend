package com.capstone.parking.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ParkingSpace {

    private Point section;

    private int index;

    public static ParkingSpace of(Point section, int index) {
        return new ParkingSpace(section, index);
    }

    public static ParkingSpace create() {
        return new ParkingSpace(new Point(2,0), 2);
    }
}
