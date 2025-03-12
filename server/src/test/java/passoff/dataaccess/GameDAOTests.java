package passoff.dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
            Assertions.assertEquals(expected.game(), game1.game());

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
            Assertions.assertTrue(games.contains(expected1));

            GameData expected2 = new GameData(9876, null, null, "game2", chessGame2);
            Assertions.assertTrue(games.contains(expected2));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failListGames() {
        // what to do?
    }

    @Test
    public void successGetAllIDs() {

    }

    @Test
    public void failGetAllIDs() {

    }

    @Test
    public void successCreateNewGame() {

    }

    @Test
    public void failCreateNewGame() {

    }

    @Test
    public void successUpdateGame() {

    }

    @Test
    public void failUpdateGame() {

    }

    @Test
    public void successClearGames() {

    }
}
