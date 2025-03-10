package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class SqlGameDAO extends SqlDataAccess implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        SqlDataAccess.createTable(TableType.GAME);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    var id = rs.getInt("gameId");
                    var wun = rs.getString("whiteUsername");
                    var bun = rs.getString("blackUsername");
                    var name = rs.getString("gameName");

                    // get the json string representing the chess game and deserialize it
                    var json = rs.getString("gameJson");
                    var gson = new Gson();
                    var game = gson.fromJson(json, ChessGame.class);

                    return new GameData(id, wun, bun, name, game);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Set<Integer> getAllGameIDs() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID FROM game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    Set ids = (Set) rs.getArray("gameID"); // fix this

                    return ids;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createNewGame(String gameName, int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(String username, String playerColor, Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
