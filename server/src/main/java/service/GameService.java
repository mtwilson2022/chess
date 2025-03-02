package service;

import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import response.*;
import request.*;
import model.*;

import java.util.List;
import java.util.Random;


public class GameService {

    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResponse listGames(ListGamessRequest req) throws UnauthorizedException {
        var token = authDAO.getAuth(req.authToken());
        if (token == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        List<GameData> games = gameDAO.listGames();
        return new ListGamesResponse(games);
    }

    public CreateGameResponse createGame(CreateGameRequest req) throws UnauthorizedException, BadRequestException {
        if (req.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        var token = authDAO.getAuth(req.authToken());
        if (token == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        var gameName = req.gameName();
        var rand = new Random();
        var gameID = rand.nextInt(1000, 10000);
        gameDAO.createNewGame(gameName, gameID);
        return new CreateGameResponse(gameID);
    }

    public JoinGameResponse joinGame(JoinGameRequest req) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        // bad request if invalid team color (e.g. "GREEN") or nonexistent gameID entered
        if (!req.playerColor().equalsIgnoreCase("WHITE") && !req.playerColor().equalsIgnoreCase("BLACK")) {
            throw new BadRequestException("Error: bad request");
        } else if (!gameDAO.getAllGameIDs().contains(req.gameID())) {
            throw new BadRequestException("Error: bad request");
        }

        var token = authDAO.getAuth(req.authToken());
        if (token == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        var username = token.username();

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
        return new JoinGameResponse();
    }

}
