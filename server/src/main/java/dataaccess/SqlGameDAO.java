package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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
                    if (rs.next()) {
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
                    else {
                        throw new DataAccessException("Nonexistent gameID");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        Set<Integer> ids = getAllGameIDs();
        List<GameData> gamesList = new ArrayList<>();

        for (int id : ids) {
            var game = getGame(id);
            gamesList.add(game);
        }

        return gamesList;
    }

    @Override
    public Set<Integer> getAllGameIDs() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID FROM game";

            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    Set<Integer> ids = new HashSet<>();

                    while (rs.next()) {
                        int id = rs.getInt("gameID");
                        ids.add(id);
                    }
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
            var gson = new Gson();
            var json = gson.toJson(new ChessGame());

            String statement = "INSERT INTO game (gameID, gameName, gameJson) VALUES (?, ?, ?)";

            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, gameName);
                preparedStatement.setString(3, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(String username, String playerColor, Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (playerColor.equalsIgnoreCase("WHITE")) {
                String statement = "UPDATE game SET whiteUsername = ? WHERE gameID = ?";

                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            } else {
                String statement = "UPDATE game SET blackUsername = ? WHERE gameID = ?";

                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /*
    Phase 6 gameplay functionality. Methods used by the WebSocketHandler.
     */

    @Override
    public void updateChessGame(Integer gameID, String gameJson) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE game SET gameJson = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gameJson);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
