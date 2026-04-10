package com.silicon.service;

import com.silicon.enums.GameStatus;
import com.silicon.model.GameSession;
import com.silicon.repositories.GameSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;

    public GameSession startNewGame(){

        GameSession game = GameSession.builder()
                .cash(100)
                .dayNumber(1)
                .bugs(0)
                .coffee(7)
                .morale(100)
                .currentLocationIndex(0)
                .hype(100)
                .progress(0)
                .status(GameStatus.IN_PROGRESS)
                .build();
        GameSession saved = gameSessionRepository.save(game);

        return saved;
    }

    public GameSession findGameById(UUID id) {

        GameSession gameId = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        return gameId;
    }

    public GameSession travel(UUID id) {

        GameSession game = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        if (game.getCurrentLocationIndex() == 6) {
            return game;
        }

        game.setCurrentLocationIndex(game.getCurrentLocationIndex() + 1);
        game.setDayNumber(game.getDayNumber() + 1);
        game.setProgress((game.getCurrentLocationIndex() * 100) / 6);
        game.setCoffee(game.getCoffee() - 1);

        if (game.getCurrentLocationIndex() == 6) {
            game.setStatus(GameStatus.WON);
        } else if (game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        GameSession saved = gameSessionRepository.save(game);

        return saved;
    }
}
