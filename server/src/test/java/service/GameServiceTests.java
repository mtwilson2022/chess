package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.*;

import model.AuthData;

import request.*;

import java.util.UUID;

public class GameServiceTests {

    GameDAO gameDAO;
    AuthDAO authDAO;
    GameService service;
    String validAuth;

    @BeforeEach
    public void SetUp() {
        this.gameDAO = new MemGameDAO();
        this.authDAO = new MemAuthDAO();
        this.service = new GameService(gameDAO, authDAO);

        gameDAO.createNewGame("game1", 1111);
        gameDAO.updateGame("Spiderman", "WHITE", 1111);
        gameDAO.updateGame("JJ Jamison", "BLACK", 1111);

        gameDAO.createNewGame("game2", 2222);
        gameDAO.updateGame("Billy the Kid", "WHITE", 2222);

        gameDAO.createNewGame("game3", 3333);

        validAuth = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(validAuth, "Spiderman"));
    }

    @Test
    public void successListGames() {
        var req = new ListGamessRequest(validAuth);
        try {
            var games = service.listGames(req);
            Assertions.assertEquals(3, games.gamesList().size());
        } catch (UnauthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failListGames() {
        var invalidReq = new ListGamessRequest("invalid authToken!");
        Assertions.assertThrows(UnauthorizedException.class, () -> service.listGames(invalidReq));
    }

    @Test
    public void successCreateGame() {
        var req = new CreateGameRequest(validAuth, "new game");
        try {
            var res = service.createGame(req);
            Assertions.assertEquals(4, gameDAO.listGames().size());
            Assertions.assertInstanceOf(Integer.class, res.gameID());
        } catch (BadRequestException | UnauthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failCreateGame() {
        var reqBadToken = new CreateGameRequest("bad auth", "name");
        Assertions.assertThrows(UnauthorizedException.class, () -> service.createGame(reqBadToken));
        var reqNoName = new CreateGameRequest(validAuth, null);
        Assertions.assertThrows(BadRequestException.class, () -> service.createGame(reqNoName));
    }

    @Test
    public void successJoinGame() {
        var req1 = new JoinGameRequest(validAuth, "BLACK", 2222);
        var req2 = new JoinGameRequest(validAuth, "WHITE", 3333);
        try {
            service.joinGame(req1);
            Assertions.assertEquals("Spiderman", gameDAO.getGame(2222).blackUsername());
            Assertions.assertEquals("Billy the Kid", gameDAO.getGame(2222).whiteUsername());

            service.joinGame(req2);
            Assertions.assertEquals("Spiderman", gameDAO.getGame(3333).whiteUsername());
            Assertions.assertNull(gameDAO.getGame(3333).blackUsername());
        } catch (BadRequestException | UnauthorizedException | AlreadyTakenException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failJoinGame() {
        var badReq1 = new JoinGameRequest(validAuth, "WHITE", 9009); // not an existent gameID
        Assertions.assertThrows(BadRequestException.class, () -> service.joinGame(badReq1));

        var badReq2 = new JoinGameRequest(validAuth, "rainbow", 1111); // not a player color
        Assertions.assertThrows(BadRequestException.class, () -> service.joinGame(badReq2));

        var reqBadToken = new JoinGameRequest("bad auth", "BLACK", 3333);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.joinGame(reqBadToken));

        var reqPlayerFilled = new JoinGameRequest(validAuth, "WHITE", 1111);
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.joinGame(reqPlayerFilled));
    }
}
