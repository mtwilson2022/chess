package handler;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;
import service.BadRequestException;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import request.*;

public class UserHandler extends HttpHandler {

    private final UserService service;
    private final Gson gson;

    public UserHandler(UserService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public Object register(Request req, Response res) throws DataAccessException {
        RegisterRequest registerReq = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResponse registerRes = service.register(registerReq);
            return sendSuccessfulResponse(registerRes, res, gson);

        } catch (AlreadyTakenException ate) {
            String errMessage = ate.getMessage();
            return sendAlreadyTakenResponse(errMessage, res, gson);

        } catch (BadRequestException bre) {
            String errMessage = bre.getMessage();
            return sendBadRequestResponse(errMessage, res, gson);
        }
    }

    public Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginReq = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResponse loginRes = service.login(loginReq);
            return sendSuccessfulResponse(loginRes, res, gson);

        } catch (UnauthorizedException ue) {
            String errMessage = ue.getMessage();
            return sendUnauthorizedResponse(errMessage, res, gson);
        }
    }

    public Object logout(Request req, Response res) throws DataAccessException {
        String token = req.headers("Authorization");
        var logoutReq = new LogoutRequest(token);
        try {
            LogoutResponse logoutRes = service.logout(logoutReq);
            return sendSuccessfulResponse(logoutRes, res, gson);

        } catch (UnauthorizedException ue) {
            String errMessage = ue.getMessage();
            return sendUnauthorizedResponse(errMessage, res, gson);
        }
    }
}
