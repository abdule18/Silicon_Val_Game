package com.silicon.service;

import com.silicon.enums.EventType;
import com.silicon.enums.GameStatus;
import com.silicon.model.GameSession;
import com.silicon.model.Location;
import com.silicon.repositories.GameSessionRepository;
import com.silicon.repositories.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final WeatherService weatherService;
    private final LocationRepository locationRepository;

    private static final int STARTING_CASH = 100;
    private static final int FINAL_LOCATION_INDEX = 9;
    private static final int MARKETING_COST = 15;
    private static final int TRAVEL_BUG_INCREASE = 2;

    public GameSession startNewGame(){

        GameSession game = GameSession.builder()
                .cash(STARTING_CASH)
                .dayNumber(1)
                .bugs(0)
                .coffee(15)
                .morale(80)
                .currentLocationIndex(0)
                .hype(20)
                .progress(0)
                .status(GameStatus.IN_PROGRESS)
                .build();

        return gameSessionRepository.save(game);
    }

    public GameSession findGameById(UUID id) {

        return gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));
    }

    private EventType getRandomEvent() {
        EventType[] events = EventType.values();
        int index = (int) (Math.random() * events.length);
        return events[index];
    }

    private void applyRandomEvent(GameSession game) {
        EventType event = getRandomEvent();

        switch (event) {
            case VC_PITCH -> {
                game.setCash(Math.min(200, game.getCash() + 15));
                game.setHype(Math.min(100, game.getHype() + 10));
                game.setBugs(game.getBugs() + 1);
            }
            case TEAM_BURNOUT -> {
                game.setMorale(Math.max(0, game.getMorale() - 8));
                game.setBugs(game.getBugs() + 1);
            }
            case BUG_BREAKTHROUGH -> {
                game.setBugs(Math.max(0, game.getBugs() - 2));
                game.setMorale(Math.min(100, game.getMorale() + 3));
            }
        }
    }

    public GameSession travel(UUID id) {

        GameSession game = getGame(id);

        if (game.getCurrentLocationIndex() == FINAL_LOCATION_INDEX) {
            game.setStatus(GameStatus.WON);
        } else if (game.getCoffee() <= 0 || game.getMorale() <= 0 || game.getCash() <= 0) {
            game.setStatus(GameStatus.LOST);
        }



        game.setCurrentLocationIndex(game.getCurrentLocationIndex() + 1);
        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(game.getBugs() + TRAVEL_BUG_INCREASE);
        game.setProgress((game.getCurrentLocationIndex() * 100) / FINAL_LOCATION_INDEX);
        Location location = locationRepository.findByRouteIndex(game.getCurrentLocationIndex());
        weatherService.applyWeatherEffects(game, location.getLatitude(), location.getLongitude());
        game.setCoffee(Math.max(0, game.getCoffee() - 1));

        applyRandomEvent(game);

        if (game.getCurrentLocationIndex() == FINAL_LOCATION_INDEX) {
            game.setStatus(GameStatus.WON);
        } else if (game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    public GameSession rest(UUID id) {

        GameSession game = getGame(id);

        if (isGameOver(game)) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setMorale(Math.min(100, game.getMorale() + 10));

        if (game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    public GameSession workOnProduct(UUID id) {

        GameSession game = getGame(id);

        if (isGameOver(game)) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(Math.max(0, game.getBugs() - 3));
        game.setMorale(Math.max(0, game.getMorale() - 2));
        game.setCoffee(Math.max(0, game.getCoffee() - 1));

        if (game.getCoffee() <= 0 || game.getMorale() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    public GameSession marketingPush(UUID id) {

        GameSession game = getGame(id);

        if (isGameOver(game)) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setCash(Math.max(0, game.getCash() - MARKETING_COST));
        game.setCoffee(Math.max(0, game.getCoffee() - 1));

        if (game.getBugs() > 5) {
            game.setHype(Math.min(100, game.getHype() + 5));
        } else {
            game.setHype(Math.min(100, game.getHype() + 10));
        }

        if (game.getCash() <= 0 || game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    // =================  Helper methods =======================
    private GameSession getGame(UUID id) {
        return gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));
    }

    private boolean isGameOver(GameSession game) {
        return game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST;
    }

}
