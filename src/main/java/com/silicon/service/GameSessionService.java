package com.silicon.service;

import com.silicon.client.dto.GameSessionResponseDTO;
import com.silicon.enums.EventType;
import com.silicon.enums.GameStatus;
import com.silicon.enums.Move;
import com.silicon.model.GameSession;
import com.silicon.model.Location;
import com.silicon.repositories.GameSessionRepository;
import com.silicon.repositories.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
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

    // Creates the starting game state
    public GameSession startNewGame() {
        GameSession game = GameSession.builder()
                .cash(STARTING_CASH)
                .dayNumber(1)
                .bugs(0)
                .coffee(15)
                .morale(50)
                .currentLocationIndex(0)
                .hype(20)
                .progress(0)
                .status(GameStatus.IN_PROGRESS)
                .build();

        return gameSessionRepository.save(game);
    }

    // Normal frontend fetch: no new event happened, so eventMessage is null
    public GameSessionResponseDTO findGameById(UUID id) {
        GameSession game = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        return buildResponse(game, null);
    }

    // Builds the response object the frontend uses
    private GameSessionResponseDTO buildResponse(GameSession game, String eventMessage) {
        Location location = locationRepository.findByRouteIndex(game.getCurrentLocationIndex());

        // Weather summary is only for display on the frontend
        String weatherSummary = weatherService.getWeatherSummary(
                location.getLatitude(),
                location.getLongitude()
        );

        String resultMessage = null;
        boolean gameOver = false;

        if (game.getStatus() == GameStatus.WON) {
            resultMessage = "🎉 You made it to San Francisco and secured funding!";
            gameOver = true;
        } else if (game.getStatus() == GameStatus.LOST) {
            resultMessage = "💀 Your startup failed. Better luck next time!";
            gameOver = true;
        }

        return GameSessionResponseDTO.builder()
                .dayNumber(game.getDayNumber())
                .cityName(location.getCityName())
                .description(location.getDescription())
                .cash(game.getCash())
                .morale(game.getMorale())
                .coffee(game.getCoffee())
                .hype(game.getHype())
                .bugs(game.getBugs())
                .weatherSummary(weatherSummary)
                .eventMessage(eventMessage)   // send random event to frontend
                .resultMessage(resultMessage)
                .gameOver(gameOver)
                .build();
    }

    // Picks one random event from the EventType enum
    private EventType getRandomEvent() {
        EventType[] events = EventType.values();
        Random random = new Random();
        return events[random.nextInt(events.length)];
    }

    // Applies the event to the game state and returns a message for the UI
    private String applyRandomEvent(GameSession game) {
        EventType event = getRandomEvent();

        return switch (event) {
            case VC_PITCH -> {
                game.setCash(Math.min(200, game.getCash() + 15));
                game.setHype(Math.min(100, game.getHype() + 10));
                game.setBugs(game.getBugs() + 1);
                yield "🎤 VC Pitch! Cash and hype increased.";
            }
            case TEAM_BURNOUT -> {
                game.setMorale(Math.max(0, game.getMorale() - 8));
                game.setBugs(game.getBugs() + 1);
                yield "😴 Team burnout! Morale dropped.";
            }
            case BUG_BREAKTHROUGH -> {
                game.setBugs(Math.max(0, game.getBugs() - 2));
                game.setMorale(Math.min(100, game.getMorale() + 3));
                yield "🐛 Breakthrough! Bugs reduced.";
            }
        };
    }

    // Travel moves the player forward and applies weather + random event
    public GameSession travel(UUID id) {
        GameSession game = getGame(id);

        // Do nothing if the game already ended
        if (isGameOver(game)) {
            return game;
        }

        game.setCurrentLocationIndex(game.getCurrentLocationIndex() + 1);
        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(game.getBugs() + TRAVEL_BUG_INCREASE);

        // Progress is based on route position
        game.setProgress((game.getCurrentLocationIndex() * 100) / FINAL_LOCATION_INDEX);

        Location location = locationRepository.findByRouteIndex(game.getCurrentLocationIndex());

        // Weather changes the game stats during travel
        weatherService.applyWeatherEffects(game, location.getLatitude(), location.getLongitude());

        // Travel consumes coffee
        game.setCoffee(Math.max(0, game.getCoffee() - 1));

        // Random event happens after travel
        String eventMessage = applyRandomEvent(game);

        // Check for win/loss after all travel effects are applied
        if (game.getCurrentLocationIndex() == FINAL_LOCATION_INDEX) {
            game.setStatus(GameStatus.WON);
        } else if (game.getCoffee() <= 0 || game.getMorale() <= 0 || game.getCash() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    // Rest increases morale
    public GameSession rest(UUID id) {
        GameSession game = getGame(id);

        if (isGameOver(game)) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setMorale(Math.min(100, game.getMorale() + 10));

        // Keep this if resting should still be able to lose when coffee is already empty
        if (game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    // Work reduces bugs but costs morale and coffee
    public GameSession workOnProduct(UUID id) {
        GameSession game = getGame(id);

        if (isGameOver(game)) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(Math.max(0, game.getBugs() - 3));
        game.setMorale(Math.max(0, game.getMorale() - 4));
        game.setCoffee(Math.max(0, game.getCoffee() - 1));

        if (game.getCoffee() <= 0 || game.getMorale() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        return gameSessionRepository.save(game);
    }

    // Marketing spends cash and increases hype
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

    // Main dispatcher for frontend moves
    public GameSessionResponseDTO makeMove(UUID id, Move move) {
        switch (move) {
            case TRAVEL -> {
                // We need the event message for travel, so handle it here
                GameSession game = getGame(id);

                if (isGameOver(game)) {
                    return buildResponse(game, null);
                }

                game.setCurrentLocationIndex(game.getCurrentLocationIndex() + 1);
                game.setDayNumber(game.getDayNumber() + 1);
                game.setBugs(game.getBugs() + TRAVEL_BUG_INCREASE);
                game.setProgress((game.getCurrentLocationIndex() * 100) / FINAL_LOCATION_INDEX);

                Location location = locationRepository.findByRouteIndex(game.getCurrentLocationIndex());

                weatherService.applyWeatherEffects(game, location.getLatitude(), location.getLongitude());
                game.setCoffee(Math.max(0, game.getCoffee() - 1));

                // Capture event message so frontend can display it
                String eventMessage = applyRandomEvent(game);

                if (game.getCurrentLocationIndex() == FINAL_LOCATION_INDEX) {
                    game.setStatus(GameStatus.WON);
                } else if (game.getCoffee() <= 0 || game.getMorale() <= 0 || game.getCash() <= 0) {
                    game.setStatus(GameStatus.LOST);
                }

                GameSession savedGame = gameSessionRepository.save(game);
                return buildResponse(savedGame, eventMessage);
            }
            case REST -> {
                rest(id);
                return findGameById(id);
            }
            case WORK -> {
                workOnProduct(id);
                return findGameById(id);
            }
            case MARKETING -> {
                marketingPush(id);
                return findGameById(id);
            }
        }

        throw new RuntimeException("Invalid move");
    }

    // Helper: get game by id
    private GameSession getGame(UUID id) {
        return gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));
    }

    // Helper: check if game already ended
    private boolean isGameOver(GameSession game) {
        return game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST;
    }
}