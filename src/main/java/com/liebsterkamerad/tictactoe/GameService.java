package com.liebsterkamerad.tictactoe;

import com.liebsterkamerad.tictactoe.dto.GameStateDTO;
import com.liebsterkamerad.tictactoe.model.CellState;
import com.liebsterkamerad.tictactoe.model.CompletedGame;
import com.liebsterkamerad.tictactoe.model.GameResult;
import com.liebsterkamerad.tictactoe.model.GameStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class GameService {

    private final CompletedGamesRepository completedGamesRepository;

    public GameService(CompletedGamesRepository completedGamesRepository) {
        this.completedGamesRepository = completedGamesRepository;
    }

    public GameResult evaluateGameState(ArrayList<ArrayList<CellState>> board) {
        int size = board.size();

        for (int i = 0; i < size; i++) {
            CellState rowState = board.get(i).get(0);
            CellState colState = board.get(0).get(i);

            if (rowState != CellState.EMPTY && board.get(i).stream().allMatch(cell -> cell == rowState)) {
                return new GameResult(rowState, GameStatus.WIN);
            }

            final int _i = i; // lambda requires final or effectively final variable
            if (colState != CellState.EMPTY && board.stream().allMatch(row -> row.get(_i) == colState)) {
                return new GameResult(colState, GameStatus.WIN);
            }
        }

        CellState mainDiagonalState = board.get(0).get(0);
        CellState otherDiagonalState = board.get(0).get(size - 1);

        if (mainDiagonalState != CellState.EMPTY && IntStream.range(0, size).allMatch(i -> board.get(i).get(i) == mainDiagonalState)) {
            return new GameResult(mainDiagonalState, GameStatus.WIN);
        }

        if (otherDiagonalState != CellState.EMPTY && IntStream.range(0, size).allMatch(i -> board.get(i).get(size - i - 1) == otherDiagonalState)) {
            return new GameResult(otherDiagonalState, GameStatus.WIN);
        }

        if (board.stream().flatMap(ArrayList::stream).noneMatch(cell -> cell == CellState.EMPTY)) {
            return new GameResult(null, GameStatus.DRAW);
        }

        return new GameResult(null, GameStatus.NOT_FINISHED);
    }

    public List<CompletedGame> getCompletedGames() {
        return completedGamesRepository.findAll();
    }

    public void saveCompletedGame(GameStateDTO gameStateDTO, GameResult result) {
        String winnerName = null;
        String loserName = null;
        switch (result.winnerState()) {
            case X -> {
                winnerName = gameStateDTO.playerName_X();
                loserName = gameStateDTO.playerName_O();
            }
            case O -> {
                winnerName = gameStateDTO.playerName_O();
                loserName = gameStateDTO.playerName_X();
            }
        }
        completedGamesRepository.save(
                new CompletedGame(null, winnerName, loserName, result.winnerState(), LocalDateTime.now()));
    }
}
