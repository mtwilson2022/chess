package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAOTests {

    // instance vars
    private SqlUserDAO userDAO;

    @BeforeEach
    public void configureTables() {
        try {
            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "DROP TABLE IF EXISTS user";
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }

            userDAO = new SqlUserDAO();

            try (Connection conn = DatabaseManager.getConnection()) {
                String stmt1 = "INSERT INTO user (username, password, email) VALUES ('user1', 'password', 'abc')";
                try (var preparedStatement = conn.prepareStatement(stmt1)) {
                    preparedStatement.executeUpdate();
                }
                String stmt2 = "INSERT INTO user (username, password, email) VALUES ('user2', '12345', 'xyz')";
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
            String statement = "DROP TABLE user";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successGetUser() {
        try {
            UserData user1 = userDAO.getUser("user1");
            UserData expected1 = new UserData("user1", "password", "abc");
            Assertions.assertEquals(expected1, user1);

            UserData user2 = userDAO.getUser("user2");
            UserData expected2 = new UserData("user2", "12345", "xyz");
            Assertions.assertEquals(expected2, user2);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failGetUser() {
        String badUsername = "QWERTY";
        try {
            UserData user = userDAO.getUser(badUsername);
            Assertions.assertNull(user);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successInsertUser() {
        try {
            UserData newUser = new UserData("new-user", "new-password", "email");
            userDAO.insertUser(newUser);

            Assertions.assertEquals(newUser, userDAO.getUser("new-user"));
            Assertions.assertEquals(3, userCount());

            UserData newUserSimilarData = new UserData("another-user", "password", "abc");
            userDAO.insertUser(newUserSimilarData);

            Assertions.assertEquals(newUserSimilarData, userDAO.getUser("another-user"));
            Assertions.assertEquals(4, userCount());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static int userCount() throws DataAccessException {
        int numUsers = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username FROM user";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        numUsers++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return numUsers;
    }

    @Test
    public void failInsertUser() {
        UserData existingUser = new UserData("user1", "word", "mail");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.insertUser(existingUser));

        try {
            Assertions.assertEquals(2, userCount());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successClearUsers() {
        try {
            Assertions.assertEquals(2, userCount());
            userDAO.clearAllUsers();
            Assertions.assertEquals(0, userCount());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
