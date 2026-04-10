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

        if (game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST) {
            return game;
        }

        game.setCurrentLocationIndex(game.getCurrentLocationIndex() + 1);
        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(game.getBugs() + 2);
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

    public GameSession rest(UUID id) {

        GameSession game = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        if (game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setMorale(Math.min(100, game.getMorale() + 10));
        game.setCoffee(game.getCoffee() - 1);

        if (game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        GameSession saved = gameSessionRepository.save(game);

        return saved;
    }

    public GameSession workOnProduct(UUID id) {

        GameSession game = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        if (game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setBugs(Math.max(0, game.getBugs() - 3));
        game.setMorale(game.getMorale() - 2);
        game.setCoffee(game.getCoffee() - 1);

        if (game.getCoffee() <= 0 || game.getMorale() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        GameSession saved = gameSessionRepository.save(game);

        return saved;
    }

    public GameSession marketingPush(UUID id) {

        GameSession game = gameSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game id not found"));

        if (game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST) {
            return game;
        }

        game.setDayNumber(game.getDayNumber() + 1);
        game.setCash(game.getCash() - 10);
        game.setCoffee(game.getCoffee() - 1);

        if (game.getBugs() > 5) {
            game.setHype(Math.max(100, game.getHype() + 5));
        } else {
            game.setHype(Math.max(10, game.getHype() + 10));
        }

        if (game.getCash() <= 0 || game.getCoffee() <= 0) {
            game.setStatus(GameStatus.LOST);
        }

        GameSession saved = gameSessionRepository.save(game);

        return saved;
    }

}
