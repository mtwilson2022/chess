package server;

import dataaccess.*;
import service.*;
import handler.*;
import com.google.gson.Gson;
import spark.*;

import java.util.Map;

public class Server {

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        var userDAO = new MemUserDAO();
        var authDAO = new MemAuthDAO();
        var gameDAO = new MemGameDAO();

        clearHandler = new ClearHandler(new ClearService(userDAO, gameDAO, authDAO));
        userHandler = new UserHandler(new UserService(userDAO, authDAO));
        gameHandler = new GameHandler(new GameService(gameDAO, authDAO));
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Endpoints and exceptions
        Spark.delete("/db", clearHandler::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(Exception.class, this::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object errorHandler(Exception e, Request ignoredReq, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}
