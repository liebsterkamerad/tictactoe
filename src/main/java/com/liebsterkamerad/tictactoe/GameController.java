package com.liebsterkamerad.tictactoe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game-state-evaluation")
    private ResponseEntity<?> evaluateGameState(@RequestBody GameState gameState) {
        return ResponseEntity.ok(gameService.evaluateGameState(gameState));
    }

    @GetMapping
    private ResponseEntity<?> getCompletedGames() {
        return ResponseEntity.ok(gameService.getCompletedGames());
    }
}
