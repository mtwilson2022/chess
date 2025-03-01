package handler;

import dataaccess.AlreadyTakenException;
import response.FailResponse;
import service.BadRequestException;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import request.*;

public class UserHandler {

    private final UserService service;
    private final Gson gson;

    public UserHandler(UserService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public Object register(Request req, Response res) {
        var registerReq = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            var registerRes = service.register(registerReq);
            res.body(gson.toJson(registerRes));
            res.status(200);
            return gson.toJson(registerRes);

        } catch (AlreadyTakenException ate) {
            String errMessage = ate.getMessage();
            res.body(errMessage);
            res.status(403);
            return gson.toJson(new FailResponse(errMessage));

        } catch (BadRequestException bre) {
            return null; // TODO: change
        }
    }

//    public Object login(Request req, Response res) {
//
//    }
//
//    public Object logout(Request req, Response res) {
//        // use req.header("...") to get auth
//    }
}
