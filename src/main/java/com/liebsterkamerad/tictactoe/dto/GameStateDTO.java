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
    }
}
