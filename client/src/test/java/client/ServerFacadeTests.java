package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.junit.jupiter.api.*;
import result.RegisterResult;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    public void clearDBTables() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String[] statements = {"TRUNCATE TABLE game", "TRUNCATE TABLE auth", "TRUNCATE TABLE user"};
            for (String statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void successRegister() {
        try {
            var registerResult = registerP1();
            assertTrue(registerResult.authToken().length() > 10);
            assertEquals("player1", registerResult.username());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failRegister() {
        assertThrows(ResponseException.class, () -> facade.register("player1", null, "email"));
        assertThrows(ResponseException.class, () -> facade.register(null, null, null));
        try {
            facade.register("player2", "password", "p1@email.com");
            assertThrows(ResponseException.class, () -> facade.register("player2", "word", "email2"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void successLogin() {
        try {
            registerP1();
            var loginResult = facade.login("player1", "password");
            assertTrue(loginResult.authToken().length() > 10);
            assertEquals("player1", loginResult.username());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failLogin() {
        assertThrows(ResponseException.class, () -> facade.login(null, null));
        assertThrows(ResponseException.class, () -> facade.login("player1", "wrong"));
    }

    @Test
    void successLogout() {
        try {
            var registerResult = registerP1();
            assertEquals(1, authCount());

            facade.logout(registerResult.authToken());
            assertEquals(0, authCount());
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failLogout() {
        assertThrows(ResponseException.class, () -> facade.logout(null));
        assertThrows(ResponseException.class, () -> facade.logout("not an auth"));
    }

    @Test
    void successListGames() {

    }

    @Test
    void failListGames() {

    }

    @Test
    void successCreateGame() {

    }

    @Test
    void failCreateGame() {

    }

    @Test
    void successJoinGame() {

    }

    @Test
    void failJoinGame() {

    }

    @Test
    void successClear() {

    }

    private RegisterResult registerP1() throws ResponseException {
        return facade.register("player1", "password", "p1@email.com");
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
}
