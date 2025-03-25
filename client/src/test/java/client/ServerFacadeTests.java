package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import result.RegisterResult;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static dataaccess.AuthDAOTests.authCount;

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
        try {
            var registerResult = registerP1();
            var gameDAO = new SqlGameDAO();
            gameDAO.createNewGame("new game", 1001);
            gameDAO.createNewGame("new game", 1221);
            GameData[] games = facade.listGames(registerResult.authToken());

            assertTrue(games[0].gameID() == 1001 || games[0].gameID() == 1221);
            assertEquals("new game", games[0].gameName());
            assertNull(games[0].whiteUsername());
            assertEquals(2, games.length);

        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failListGames() {
        assertThrows(ResponseException.class, () -> facade.listGames("bad token"));
    }

    @Test
    void successCreateGame() {
        try {
            var registerResult = registerP1();
            String token = registerResult.authToken();

            int createResult = createG1(token);
            assertTrue(1000 <= createResult && createResult < 10000);

            facade.createGame(token, "new game"); // same as createG1
            facade.createGame(token, "game number 3");
            assertEquals(3, gameCount());
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failCreateGame() {
        try {
            var registerResult = registerP1();
            String token = registerResult.authToken();

            assertThrows(ResponseException.class, () -> facade.createGame(token, null));
            assertThrows(ResponseException.class, () -> facade.createGame(null, "name"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void successJoinGame() {
        try {
            var registerResult = registerP1();
            String token = registerResult.authToken();
            int id = createG1(token);

            facade.joinGame(token, "white", id);
            assertEquals("player1", facade.listGames(token)[0].whiteUsername());
            assertNull(facade.listGames(token)[0].blackUsername());

            facade.joinGame(token, "black", id);
            assertEquals("player1", facade.listGames(token)[0].whiteUsername());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failJoinGame() {
        assertThrows(ResponseException.class, () -> facade.joinGame(null, "white", 1234));
        try {
            String token = registerP1().authToken();
            int id = createG1(token);

            // join with invalid player color
            assertThrows(ResponseException.class, () -> facade.joinGame(token, "green", id));

            // join twice as the same color
            facade.joinGame(token, "white", id);
            assertThrows(ResponseException.class, () -> facade.joinGame(token, "white", id));

        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void successClear() {
        try {
            registerP1();
            assertEquals(1, authCount());

            facade.clear();

            assertEquals(0, authCount());

            // player1 should no longer be registered
            assertThrows(ResponseException.class, () -> facade.login("player1", "password"));

        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private RegisterResult registerP1() throws ResponseException {
        return facade.register("player1", "password", "p1@email.com");
    }

    private int createG1(String authToken) throws ResponseException {
        return facade.createGame(authToken, "new game");
    }

    private static int gameCount() throws DataAccessException {
        int numGames = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID FROM game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        numGames++;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return numGames;
    }
}
