# Tic Tac Toe game challenge

I am happy to present you my solution of the game challenge. It was a lot of fun to implement it!

### How to start the application

1. Clone the repository
2. Make sure Docker desktop is installed and running on your machine
3. Build the application through opening it with IntelliJ or running `./gradlew build` or `./gradlew.bat build` (Windows)
   1. *gradlew* wrapper is included in the root of the project
2. Run the application with `./gradlew bootTestRun` or `./gradlew.bat bootTestRun` (Windows)
   4. The application will start at port 8080
   5. It also automatically starts a PostgreSQL database in a Docker container 
      6. you can access it with your database viewer when running ```docker ps``` and then checking to which local port the container is mapped
      7. 0.0.0.0:51473->5432/tcp means you can access it at localhost:51473
      8. ...using database name == username == password == "test"
5. Test that API controller is working using HTTP test collections under ./src/main/resources
   6. ...in IntelliJ format
   7. ...in Postman format
8. Run integration tests tests using `./gradlew test`
   9. Most interesting is /src/test/java/com/liebsterkamerad/tictactoe/GameControllerTest.java
      10. ...it contains a bunch of different game state situations

*The application only accepts JSON requests with the board in the following format, any attempt to pass wrong data
would result in an exception that is communicated back to the user*

The size of board starts from 3x3 and can be extended to (virtually) any size. My tests include 7x7 board.
- Board should be quadratic, i.e. the number of rows should be equal to the number of columns.
- Every WIN is stored in the database and can be retrieved as part of completed games list
- I have decided to also store DRAW states in the database, as they are also a valid game outcome

```json
// each cell can be either "X", "O" or "_"(empty)
{
   "board": [
      ["O", "X", "O"],
      ["_", "O", "X"],
      ["_", "X", "O"]
   ],
   "playerName_X": "Alice",
   "playerName_O": "Bob"
}
```
For example the following request...
```json
{
  "board": [
    ["X", "O", "_"],
    ["i", "_", "_"],
    ["_", "_", "_"]
  ],
  "playerName_X": "Alice",
  "playerName_O": "Bob"
}
```
...would result in an error

```json
{
    "path": "/api/game-state-evaluation",
    "error": "Bad Request",
    "message": "JSON parse error: Cannot construct instance of `com.liebsterkamerad.tictactoe.model.CellState`, problem: Invalid symbol: i",
    "timestamp": "2024-05-02T04:04:56.180849",
    "status": 400
}
```