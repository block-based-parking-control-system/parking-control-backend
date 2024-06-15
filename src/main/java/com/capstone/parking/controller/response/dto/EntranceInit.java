package com.capstone.parking.controller.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EntranceInit {

    private List<Point> entranceRoute;

    private ParkingSpace parkingSpace;

    public static EntranceInit create() {
        return new EntranceInit(
                List.of(new Point(6, 11), new Point(5,11), new Point(5,10), new Point(4,10), new Point(3,10), new Point(3,9), new Point(3,8), new Point(3,7), new Point(3, 6), new Point(3, 5), new Point(3, 4), new Point(3, 3), new Point(3, 2), new Point(2, 2), new Point(2,1)),
                ParkingSpace.create()
        );
    }

}
