package com.silicon.client.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class WeatherResponse {

    private CurrentWeather current;
}
