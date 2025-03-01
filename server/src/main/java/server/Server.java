package server;

import dataaccess.MemAuthDAO;
import dataaccess.MemGameDAO;
import dataaccess.MemUserDAO;
import dataaccess.UserDAO;
import service.*;
import handler.*;
import com.google.gson.Gson;
import spark.*;

public class Server {

    // instance vars (Handler objects) go here
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

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::clear);
        Spark.post("/user", userHandler::register);
//        Spark.post("/session", userHandler::login);
//        Spark.delete("/session", userHandler::logout);
        // game endpoints

        // exceptions

//            res.body(gson.toJson(registerRes)); TODO: just change status codes in handler without returning result? How does server return stuff to client?
//            res.status(200);
//            return res;

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
