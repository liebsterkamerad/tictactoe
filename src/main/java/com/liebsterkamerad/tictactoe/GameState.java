package com.liebsterkamerad.tictactoe;

import java.util.ArrayList;

public record GameState(ArrayList<ArrayList<CellState>> board, String X_PlayerName, String O_PLayerName) {
    public GameState {
        for (var row : board) {
            if (row.size() != board.size()) {
                throw new IllegalArgumentException("Board should be square. All rows must have the same number of columns.");
            }
        }

        if (board.size() < 3) {
            throw new IllegalArgumentException("Board should be at least 3x3.");
        }

        if (X_PlayerName.isBlank() || O_PLayerName.isBlank()) {
            throw new IllegalArgumentException("Player names should not be blank.");
        }

        if (X_PlayerName.equals(O_PLayerName)) {
            throw new IllegalArgumentException("Player names should be different.");
        }
    }
}
