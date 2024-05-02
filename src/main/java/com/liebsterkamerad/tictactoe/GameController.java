package com.liebsterkamerad.tictactoe;

import com.liebsterkamerad.tictactoe.dto.GameStateDTO;
import com.liebsterkamerad.tictactoe.model.GameResult;
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
    private ResponseEntity<?> evaluateGameState(@RequestBody GameStateDTO gameStateDTO) {
        GameResult result = gameService.evaluateGameState(gameStateDTO.board());

        switch (result.status()) {
            case WIN -> {
                gameService.saveCompletedGame(gameStateDTO, result);
                return ResponseEntity.ok(
                        switch (result.winnerState()) {
                            case X -> "Player X - %s - wins!".formatted(gameStateDTO.playerName_X());
                            case O -> "Player O - %s - wins!".formatted(gameStateDTO.playerName_O());
                            default -> throw new IllegalStateException("Unexpected value: " + result.winnerState());
                        }
                );
            }
            case DRAW -> {
                gameService.saveCompletedGame(gameStateDTO, result);
                return ResponseEntity.ok("It's a draw!");
            }
            case NOT_FINISHED -> {
                return ResponseEntity.ok("Game is not finished yet!");
            }
        }

        return ResponseEntity.badRequest().body("Invalid game state!");
    }

    @GetMapping("/completed-games")
    private ResponseEntity<?> getCompletedGames() {
        return ResponseEntity.ok(gameService.getCompletedGames());
    }
}
