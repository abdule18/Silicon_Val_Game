package com.silicon.service;

import com.silicon.enums.GameStatus;
import com.silicon.model.GameSession;
import com.silicon.repositories.GameSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
