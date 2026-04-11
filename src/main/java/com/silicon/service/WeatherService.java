package com.silicon.service;

import com.silicon.client.WeatherClient;
import com.silicon.client.dto.CurrentWeather;
import com.silicon.client.dto.WeatherResponse;
import com.silicon.model.GameSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient;

    public void applyWeatherEffects(GameSession game, double lat, double lon) {

        WeatherResponse response = weatherClient.getCurrentWeather(lat, lon);

        CurrentWeather current = response.getCurrent();

        if (current == null) {
            return;
        }

        if (current.getWind_speed_10m() > 10) {
            game.setCoffee(Math.max(0, game.getCoffee() -1));
        }

        if (current.getTemperature_2m() < 50) {
            game.setMorale(Math.max(0, game.getMorale() - 3));
        }

        if (current.getWeather_code() == 0) {
            game.setMorale(Math.min(100, game.getMorale() + 2));
        }
    }
}
