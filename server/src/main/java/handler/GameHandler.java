package handler;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import service.BadRequestException;
import service.GameService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import request.*;

public class GameHandler extends HttpHandler {
    private final GameService service;
    private final Gson gson;

    public GameHandler(GameService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public Object listGames(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");
        var listGamesReq = new ListGamesRequest(token);
        try {
            ListGamesResult listGamesRes = service.listGames(listGamesReq);
            return sendSuccessfulResponse(listGamesRes, res, gson);

        } catch (UnauthorizedException ue) {
            String errMessage = ue.getMessage();
            return sendUnauthorizedResponse(errMessage, res, gson);
        }
    }

    public Object createGame(Request req, Response res) throws DataAccessException {
        var initialReq = gson.fromJson(req.body(), CreateGameRequest.class);
        String token = req.headers("Authorization");
        CreateGameRequest createGameReq = initialReq.setAuthToken(token);

        try {
            CreateGameResult createGameRes = service.createGame(createGameReq);
            return sendSuccessfulResponse(createGameRes, res, gson);

        } catch (UnauthorizedException ue) {
            String errMessage = ue.getMessage();
            return sendUnauthorizedResponse(errMessage, res, gson);

        } catch (BadRequestException bre) {
            String errMessage = bre.getMessage();
            return sendBadRequestResponse(errMessage, res, gson);
        }
    }

    public Object joinGame(Request req, Response res) throws DataAccessException {
        var initialReq = gson.fromJson(req.body(), JoinGameRequest.class);
        String token = req.headers("Authorization");
        JoinGameRequest joinGameReq = initialReq.setAuthToken(token);

        try {
            JoinGameResult joinGameRes = service.joinGame(joinGameReq);
            return sendSuccessfulResponse(joinGameRes, res, gson);

        } catch (UnauthorizedException ue) {
            String errMessage = ue.getMessage();
            return sendUnauthorizedResponse(errMessage, res, gson);

        } catch (AlreadyTakenException ate) {
            String errMessage = ate.getMessage();
            return sendAlreadyTakenResponse(errMessage, res, gson);

        } catch (BadRequestException bre) {
            String errMessage = bre.getMessage();
            return sendBadRequestResponse(errMessage, res, gson);
        }
    }
}
