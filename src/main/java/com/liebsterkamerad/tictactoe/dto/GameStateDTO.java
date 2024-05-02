package com.liebsterkamerad.tictactoe.dto;

import com.liebsterkamerad.tictactoe.model.CellState;

import java.util.ArrayList;

public record GameStateDTO(ArrayList<ArrayList<CellState>> board, String playerName_X, String playerName_O) {
    public GameStateDTO {
        for (var row : board) {
            if (row.size() != board.size()) {
                throw new IllegalArgumentException("Board should be square. All rows must have the same number of columns.");
            }
        }

        if (board.size() < 3) {
            throw new IllegalArgumentException("Board should be at least 3x3.");
        }

        if (playerName_X.isBlank() || playerName_O.isBlank()) {
            throw new IllegalArgumentException("Player names should not be blank.");
        }

        if (playerName_X.equals(playerName_O)) {
            throw new IllegalArgumentException("Player names should be different.");
        }

        validateNoPlayerHasAdvantage(board);
    }

    private void validateNoPlayerHasAdvantage(ArrayList<ArrayList<CellState>> board) {
        int count_X = 0;
        int count_O = 0;

        for (var row : board) {
            for (var cell : row) {
                if (cell == CellState.X) {
                    count_X++;
                } else if (cell == CellState.O) {
                    count_O++;
                }
            }
        }

        if (count_X > count_O + 1) {
            throw new IllegalArgumentException(
                    "Player X has unjust advantage by %s moves.".formatted(count_X - count_O));
        } else if (count_O > count_X + 1) {
            throw new IllegalArgumentException(
                    "Player O has unjust advantage by %s moves.".formatted(count_O - count_X));
        }
    }
}
