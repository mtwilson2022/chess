package service;

import dataaccess.*;
import model.*;
import request.*;
import result.*;
import java.util.List;
import java.util.Random;


public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
        var auth = authDAO.getAuth(req.authToken());
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        List<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException, BadRequestException {
        if (req.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        var auth = authDAO.getAuth(req.authToken());
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        var gameName = req.gameName();
        var rand = new Random();
        var gameID = rand.nextInt(1000, 10000);
        gameDAO.createNewGame(gameName, gameID);
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws DataAccessException, BadRequestException {
        // bad request if invalid team color (e.g. "GREEN" or null) or nonexistent gameID entered
        if (req.playerColor() == null) {
            throw new BadRequestException("Error: bad request");
        } else if (!req.playerColor().equalsIgnoreCase("WHITE") && !req.playerColor().equalsIgnoreCase("BLACK")) {
            throw new BadRequestException("Error: bad request");
        } else if (!gameDAO.getAllGameIDs().contains(req.gameID())) {
            throw new BadRequestException("Error: bad request");
        }

        var auth = authDAO.getAuth(req.authToken());
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        var username = auth.username();

        var game = gameDAO.getGame(req.gameID());

        // verify that the chosen player color is available
        if (req.playerColor().equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
        } else if (req.playerColor().equalsIgnoreCase("BLACK")) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }
        }

        gameDAO.updateGame(username, req.playerColor(), req.gameID());
        return new JoinGameResult();
    }

}
