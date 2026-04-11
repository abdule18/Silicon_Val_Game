package com.silicon.client.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class CurrentWeather {

    private double temperature_2m;
    private double wind_speed_10m;
    private int weather_code;
}
