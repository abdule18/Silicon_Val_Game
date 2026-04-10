package com.silicon.controller;

import com.silicon.model.GameSession;
import com.silicon.service.GameSessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
@AllArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @PostMapping()
    public ResponseEntity<GameSession> startGame() {
        GameSession saved = gameSessionService.startNewGame();
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSession> getGame(@PathVariable UUID id) {
        return ResponseEntity.ok(gameSessionService.findGameById(id));
    }

    @PostMapping("/{id}/travel")
    public ResponseEntity<GameSession> travel(@PathVariable UUID id) {
        return ResponseEntity.ok(gameSessionService.travel(id));
    }

    @PostMapping("/{id}/rest")
    public ResponseEntity<GameSession> rest(@PathVariable UUID id) {
        return ResponseEntity.ok(gameSessionService.rest(id));
    }

    @PostMapping("/{id}/work")
    public ResponseEntity<GameSession> workOnProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(gameSessionService.workOnProduct(id));
    }

    @PostMapping("/{id}/marketing")
    public ResponseEntity<GameSession> marketingPush(@PathVariable UUID id) {
        return ResponseEntity.ok(gameSessionService.marketingPush(id));
    }

}
