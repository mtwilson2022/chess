package handler;

import service.GameService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;


public class GameHandler {
    private final GameService service;

    public GameHandler(GameService service) {
        this.service = service;
    }
}
