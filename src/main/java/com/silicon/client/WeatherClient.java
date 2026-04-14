package com.silicon.client;

import com.silicon.client.dto.WeatherResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class WeatherClient {

    private RestClient restClient;

    public WeatherClient() {
        this.restClient = RestClient.create();
    }

    // Calls the external weather API and maps the response into a Java DTO.
    public WeatherResponse getCurrentWeather(double lat, double lon) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + lat +
                "&longitude=" + lon +
                "&current=temperature_2m,wind_speed_10m,weather_code";

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(WeatherResponse.class);
    }
}
