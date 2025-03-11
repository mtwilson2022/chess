package passoff.dataaccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import passoff.model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AuthDAOTests {

    // instance vars
    private SqlAuthDAO authDAO;

    @BeforeAll
    public static void configureDB() {

    }

    @BeforeEach
    public void configureTables() {
        try {
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
            Assertions.assertEquals(auth1.username(), expected1.username());

            // make sure user1 is still authorized in the DB
            AuthData auth2 = authDAO.getAuth("example-auth");
            Assertions.assertEquals(auth2, expected1);

            AuthData auth3 = authDAO.getAuth("other-auth");
            AuthData expected3 = new AuthData("other-auth", "user2");
            Assertions.assertEquals(auth3, expected3);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failGetAuth() {
        // invalid auth token returns null, or change service classes to allow any sql exceptions?
    }

    @Test
    public void successInsertAuth() {
        // pretty simple. just do it
    }

    @Test
    public void failInsertAuth() {
        // try to insert an auth that already exists maybe?
        // should replace the auth for someone that already has an auth
    }

    @Test
    public void successDeleteAuth() {
        // validate an authtoken, then remove it, then make sure it's gone. Check both authToken and username
        // check for reduced size of tables
    }

    @Test
    public void failDeleteAuth() {
        // try to delete an auth that doesn't exist
    }

    @Test
    public void successClearAuths() {
        // get auths, clear them, try to get them
        // check that the table is cleared but still exists
    }
}
