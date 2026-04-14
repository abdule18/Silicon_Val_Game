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

    public String getWeatherSummary(double lat, double lon) {
        WeatherResponse response = weatherClient.getCurrentWeather(lat, lon);

        // If weather data is missing, return a safe fallback instead of failing the request.
        if (response == null || response.getCurrent() == null) {
            return "Weather unavailable";
        }

        CurrentWeather current = response.getCurrent();

        String condition;
        int code = current.getWeather_code();

        // Map raw weather codes into player-friendly text for the UI.
        if (code == 0) {
            condition = "Clear skies";
        } else if (code <= 3) {
            condition = "Cloudy";
        } else if (code >= 51 && code <= 67) {
            condition = "Rainy";
        } else {
            condition = "Unsettled weather";
        }

        return condition + ", " + current.getTemperature_2m() + "°F";
    }

    public void applyWeatherEffects(GameSession game, double lat, double lon) {
        WeatherResponse response = weatherClient.getCurrentWeather(lat, lon);

        // Skip weather-based penalties if the API response is unavailable.
        if (response == null || response.getCurrent() == null) {
            return;
        }

        CurrentWeather current = response.getCurrent();

        // Strong wind reduces coffee because travel becomes harder.
        if (current.getWind_speed_10m() > 15) {
            game.setCoffee(Math.max(0, game.getCoffee() -1));
        }

        // Cold temperatures lower morale because the journey feels tougher.
        if (current.getTemperature_2m() < 50) {
            game.setMorale(Math.max(0, game.getMorale() - 3));
        }

        // Good weather slightly improves morale.
        if (current.getWeather_code() == 0) {
            game.setMorale(Math.min(100, game.getMorale() + 2));
        }
    }
}
