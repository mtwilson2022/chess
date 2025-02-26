package server;

import service.UserService;
import spark.*;

public class Server {

    // instance vars (Handler objects) go here

    public Server() {
        // initialize handlers
            // initialize DAOs and pass them into instances of service classes, which you give to handlers
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

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
