package com.liebsterkamerad.tictactoe.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CellState {
    EMPTY,
    X,
    O;

    @JsonValue
    @Override
    public String toString() {
        return this == EMPTY ? "_" : super.toString();
    }

    @JsonCreator
    public static CellState fromString(String symbol) {
        return switch (symbol) {
            case "_" -> EMPTY;
            case "X" -> X;
            case "O" -> O;
            default -> throw new IllegalArgumentException("Invalid symbol: " + symbol);
        };
    }
}
