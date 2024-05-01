package com.liebsterkamerad.tictactoe;

import com.liebsterkamerad.tictactoe.model.CompletedGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedGamesRepository extends JpaRepository<CompletedGame, Long> {
}
