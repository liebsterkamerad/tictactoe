package com.liebsterkamerad.tictactoe;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GameControllerTest extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("0 completed games")
    public void testGetCompletedGames() throws Exception {
        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertEquals("[]", content);
                });
    }

    @Test
    @DisplayName("Game is not finished")
    public void gameIsNotFinished() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "_"],
                                                    ["_", "_", "_"],
                                                    ["_", "_", "_"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Game is not finished yet!", content);
                });

        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JSONArray games = JsonPath.parse(content).read("$");
                    assertEquals(0, games.size());
                });
    }

    @Test
    @DisplayName("Invalid game state: Player X has advantage")
    public void invalidGameStatePlayerXHasAdvantage() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "X"],
                                                    ["_", "_", "_"],
                                                    ["X", "_", "_"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    String message = JsonPath.parse(content).read("$.message");
                    assertTrue(message.contains("Player X has unjust advantage by 2 moves."));
                });
    }

    @Test
    @DisplayName("Invalid game state: Player X has advantage II")
    public void invalidGameStatePlayerXHasAdvantage_2() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "_", "_"],
                                                    ["_", "_", "_"],
                                                    ["X", "_", "_"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    String message = JsonPath.parse(content).read("$.message");
                    assertTrue(message.contains("Player X has unjust advantage by 2 moves."));
                });
    }

    @Test
    @DisplayName("Invalid game state: Player O has advantage")
    public void invalidGameStatePlayerOHasAdvantage() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["O", "_", "_"],
                                                    ["_", "O", "_"],
                                                    ["_", "_", "_"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    String message = JsonPath.parse(content).read("$.message");
                    assertTrue(message.contains("Player O has unjust advantage by 2 moves."));
                });
    }

    @Test
    @DisplayName("Player X wins 3x3")
    public void playerXwins3x3() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "X"],
                                                    ["O", "X", "O"],
                                                    ["O", "X", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player X - Alice - wins!", content);
                });

        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JSONArray games = JsonPath.parse(content).read("$");
                    assertEquals(1, games.size());
                });
    }

    @Test
    @DisplayName("Player O wins 3x3")
    public void player0wins3x3() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["O", "O", "X"],
                                                    ["O", "X", "O"],
                                                    ["O", "X", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player O - Bob - wins!", content);
                });

        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JSONArray games = JsonPath.parse(content).read("$");
                    assertEquals(1, games.size());
                });
    }

    @Test
    @DisplayName("It's a draw 3x3")
    public void itsADraw3x3() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "X"],
                                                    ["O", "X", "O"],
                                                    ["O", "X", "O"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("It's a draw!", content);
                });

        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JSONArray games = JsonPath.parse(content).read("$");
                    assertEquals(1, games.size());
                });
    }

    @Test
    @DisplayName("Play one win and one draw game")
    public void playOneWinAndOneDrawGame() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "X"],
                                                    ["O", "X", "O"],
                                                    ["O", "X", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player X - Alice - wins!", content);
                });

        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "O", "X"],
                                                    ["O", "X", "O"],
                                                    ["O", "X", "O"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("It's a draw!", content);
                });

        mockMvc.perform(get("/api/completed-games"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    JSONArray games = JsonPath.parse(content).read("$");
                    assertEquals(2, games.size());
                });
    }

    @Test
    @DisplayName("Player X wins with column on 7x7 board")
    public void playerXwinsColumn7x7() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "X", "O", "X", "O", "X", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "X"],
                                                    ["O", "X", "O", "X", "O", "O", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "X"],
                                                    ["O", "X", "O", "X", "O", "X", "O"],
                                                    ["O", "X", "O", "X", "O", "O", "O"],
                                                    ["X", "X", "X", "O", "O", "O", "O"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player X - Alice - wins!", content);
                });
    }

    @Test
    @DisplayName("Player O wins with row on 7x7 board")
    public void playerOwinsRow7x7() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["O", "O", "X", "O", "X", "X", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "X"],
                                                    ["O", "O", "O", "O", "O", "O", "O"],
                                                    ["X", "O", "X", "O", "X", "X", "X"],
                                                    ["O", "X", "O", "X", "O", "X", "O"],
                                                    ["X", "X", "O", "X", "X", "O", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "O"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player O - Bob - wins!", content);
                });
    }

    @Test
    @DisplayName("Player X wins main diagonal on 7x7 board")
    public void playerXwinsMainDiagonal7x7() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "X", "_", "O", "_", "X", "O"],
                                                    ["X", "X", "X", "O", "O", "O", "X"],
                                                    ["O", "X", "X", "X", "O", "_", "O"],
                                                    ["_", "O", "X", "X", "_", "O", "X"],
                                                    ["O", "_", "O", "X", "X", "X", "O"],
                                                    ["O", "X", "O", "_", "O", "X", "O"],
                                                    ["O", "O", "X", "O", "X", "O", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player X - Alice - wins!", content);
                });
    }

    @Test
    @DisplayName("Player O wins secondary diagonal on 7x7 board")
    public void playerOwinsSecondaryDiagonal7x7() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "X", "X", "X", "O", "X", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "X"],
                                                    ["O", "X", "O", "X", "O", "O", "O"],
                                                    ["X", "O", "X", "O", "X", "O", "X"],
                                                    ["O", "X", "O", "X", "O", "X", "O"],
                                                    ["O", "O", "O", "X", "X", "O", "O"],
                                                    ["O", "X", "X", "O", "X", "O", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("Player O - Bob - wins!", content);
                });
    }

    @Test
    @DisplayName("Draw on 7x7 board")
    public void draw7x7() throws Exception {
        mockMvc.perform(post("/api/game-state-evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "board": [
                                                    ["X", "X", "O", "X", "O", "X", "O"],
                                                    ["O", "X", "X", "O", "X", "O", "X"],
                                                    ["O", "O", "O", "X", "O", "X", "O"],
                                                    ["X", "O", "X", "O", "X", "O", "X"],
                                                    ["O", "X", "O", "X", "O", "X", "O"],
                                                    ["O", "X", "O", "X", "O", "O", "O"],
                                                    ["X", "X", "X", "O", "X", "O", "X"]
                                                ],
                                                "playerName_X": "Alice",
                                                "playerName_O": "Bob"
                                            }
                                        """
                        ))
                .andExpect(result -> {
                    assertEquals(200, result.getResponse().getStatus());
                    String content = result.getResponse().getContentAsString();
                    assertEquals("It's a draw!", content);
                });
    }
}