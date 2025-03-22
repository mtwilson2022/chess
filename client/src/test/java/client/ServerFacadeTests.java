package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "https://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void successRegister() {
        var registerResult = facade.register("player1", "password", "p1@email.com");
        assertTrue(registerResult.authToken().length() > 10);
        assertEquals("player1", registerResult.username());
    }

    @Test
    void failRegister() {

    }

    @Test
    void successLogin() {

    }

    @Test
    void failLogin() {

    }

    @Test
    void successLogout() {

    }

    @Test
    void failLogout() {

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
}
