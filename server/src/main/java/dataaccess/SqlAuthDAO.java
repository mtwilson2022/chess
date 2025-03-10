package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlAuthDAO extends SqlDataAccess implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        SqlDataAccess.createTable(TableType.AUTH);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth WHERE auth = ?"; // TODO: need to worry abt warning?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage()); // TODO: how far back should I throw stuff?
        }
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        var token = auth.authToken();
        var username = auth.username();

        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM auth WHERE auth = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken); // TODO: should use set method just for insert, or always?
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAllAuths() throws DataAccessException { // TODO: why no negative test cases? how to handle exceptions?
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
