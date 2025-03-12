package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class GameDAOTests {

    // instance vars
    private SqlGameDAO gameDAO;
    private ChessGame chessGame1;
    private ChessGame chessGame2;

    @BeforeEach
    public void configureTables() {
        try {
            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "DROP TABLE IF EXISTS game";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }

            gameDAO = new SqlGameDAO();
            chessGame1 = new ChessGame();
            chessGame2 = new ChessGame();

            var gson = new Gson();

            var json1 = gson.toJson(chessGame1);
            var json2 = gson.toJson(chessGame2);

            try (Connection conn = DatabaseManager.getConnection()) {
                String stmt1 = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, gameJson) VALUES (?, ?, ?, ?, ?)";
                try (var preparedStatement = conn.prepareStatement(stmt1)) {
                    preparedStatement.setInt(1, 1234);
                    preparedStatement.setString(2, "whiteUser");
                    preparedStatement.setString(3, "blackUser");
                    preparedStatement.setString(4, "game1");
                    preparedStatement.setString(5, json1);
                    preparedStatement.executeUpdate();
                }
                String stmt2 = "INSERT INTO game (gameID, gameName, gameJson) VALUES (?, ?, ?)";
                try (var preparedStatement = conn.prepareStatement(stmt2)) {
                    preparedStatement.setInt(1, 9876);
                    preparedStatement.setString(2, "game2");
                    preparedStatement.setString(3, json2);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void removeTables() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successGetGame() {
        try {
            GameData expected = new GameData(1234, "whiteUser", "blackUser", "game1", chessGame1);
            GameData game1 = gameDAO.getGame(1234);
            Assertions.assertEquals(expected.game().getBoard(), game1.game().getBoard());

            GameData game2  = gameDAO.getGame(9876);
            Assertions.assertNull(game2.whiteUsername());
            Assertions.assertEquals("game2", game2.gameName());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failGetGame() {
        // nonexistent gameID
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(1111));
    }

    @Test
    public void successListGames() {
        try {
            List<GameData> games = gameDAO.listGames();
            Assertions.assertEquals(2, games.size());

            GameData expected1 = new GameData(1234, "whiteUser", "blackUser", "game1", chessGame1);
            Assertions.assertEquals(games.getFirst().game().getBoard(), expected1.game().getBoard());

            GameData expected2 = new GameData(9876, null, null, "game2", chessGame2);
            Assertions.assertEquals(games.getLast().game().getBoard(), expected2.game().getBoard());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failListGames() {
        try {
            messUpTable();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.listGames());
    }

    @Test
    public void successGetAllIDs() {
        try {
            Set<Integer> ids = gameDAO.getAllGameIDs();
            Assertions.assertEquals(2, ids.size());
            Assertions.assertTrue(ids.contains(1234));
            Assertions.assertTrue(ids.contains(9876));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failGetAllIDs() {
        try {
            messUpTable();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getAllGameIDs());
    }

    @Test
    public void successCreateNewGame() {
        try {
            gameDAO.createNewGame("new game", 1001);
            gameDAO.createNewGame("new game", 1221); // can have same name, but must have diff ids
            Assertions.assertEquals(4, gameDAO.getAllGameIDs().size());
            Assertions.assertNotNull(gameDAO.getGame(1221));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failCreateNewGame() {
        // error if a new game is created with the same ID
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createNewGame("new game", 1234));
    }

    @Test
    public void successUpdateGame() {
        try {
            gameDAO.updateGame("user1", "black", 9876);
            Assertions.assertEquals("user1", gameDAO.getGame(9876).blackUsername());
            Assertions.assertNull(gameDAO.getGame(9876).whiteUsername());

            gameDAO.updateGame("spiderman", "black", 9876);
            Assertions.assertEquals("spiderman", gameDAO.getGame(9876).blackUsername());

            gameDAO.updateGame("user1", "white", 9876);
            Assertions.assertEquals("user1", gameDAO.getGame(9876).whiteUsername());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failUpdateGame() {
        // tries to update a nonexistent game. Nothing new should be created.
        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame("user1", "white", 1001));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(1001));

        try {
            messUpTable();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame("user1", "white", 1001));
    }

    @Test
    public void successClearGames() {
        try {
            Assertions.assertEquals(2, gameDAO.getAllGameIDs().size());
            gameDAO.clearAllGames();
            Assertions.assertEquals(0, gameDAO.getAllGameIDs().size());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private static void messUpTable() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DROP TABLE game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }

            String stmt = "CREATE TABLE game (id INT NOT NULL, PRIMARY KEY (id) )";
            try (var preparedStatement = conn.prepareStatement(stmt)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
