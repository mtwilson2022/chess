package passoff.dataaccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthDAOTests {

    // instance vars
    private SqlAuthDAO authDAO;

    @BeforeEach
    public void configureTables() {
        try {
            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "DROP TABLE IF EXISTS auth";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }

            authDAO = new SqlAuthDAO();

            try (Connection conn = DatabaseManager.getConnection()) {
                String stmt1 = "INSERT INTO auth (authToken, username) VALUES ('example-auth', 'user1')";
                try (var preparedStatement = conn.prepareStatement(stmt1)) {
                    preparedStatement.executeUpdate();
                }
                String stmt2 = "INSERT INTO auth (authToken, username) VALUES ('other-auth', 'user2')";
                try (var preparedStatement = conn.prepareStatement(stmt2)) {
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
            String statement = "DROP TABLE auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successGetAuth() {
        try {
            AuthData auth1 = authDAO.getAuth("example-auth");
            AuthData expected1 = new AuthData("example-auth", "user1");
            Assertions.assertEquals(expected1.username(), auth1.username());

            // make sure user1 is still authorized in the DB
            AuthData auth2 = authDAO.getAuth("example-auth");
            Assertions.assertEquals(expected1, auth2);

            AuthData auth3 = authDAO.getAuth("other-auth");
            AuthData expected3 = new AuthData("other-auth", "user2");
            Assertions.assertEquals(expected3, auth3);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failGetAuth() {
        // invalid auth token returns null
        String badToken = "this is not a good auth.";
        try {
            AuthData auth = authDAO.getAuth(badToken);
            Assertions.assertNull(auth);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successInsertAuth() {
        // insert a new auth
        try {
            AuthData newAuth = new AuthData("new-auth", "new-user");
            authDAO.insertAuth(newAuth);

            Assertions.assertEquals(newAuth, authDAO.getAuth("new-auth"));
            Assertions.assertEquals(3, authCount());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static int authCount() throws DataAccessException {
        int numAuths = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        numAuths++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return numAuths;
    }

    @Test
    public void failInsertAuth() {
        // try to insert an auth that already exists
        AuthData existingAuth = new AuthData("example-auth", "new-user");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.insertAuth(existingAuth));

        try {
            Assertions.assertEquals(2, authCount());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successDeleteAuth() {
        // remove an authToken, then make sure it's gone. Check both authToken and username
        try {
            authDAO.deleteAuth("example-auth");
            Assertions.assertNull(authDAO.getAuth("example-auth"));

            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "SELECT username FROM auth WHERE username = 'user1'";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    try (var rs = preparedStatement.executeQuery()) {
                        Assertions.assertFalse(rs.next());
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
            // check for reduced size of tables
            Assertions.assertEquals(1, authCount());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failDeleteAuth() {
        // try to delete an auth that doesn't exist
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("not-an-auth"));
    }

    @Test
    public void successClearAuths() {
        try {
            Assertions.assertEquals(2, authCount());
            authDAO.clearAllAuths();
            Assertions.assertEquals(0, authCount());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
