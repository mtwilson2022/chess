package handler;

import com.google.gson.Gson;
import response.FailResponse;

public class HttpHandler {
    protected Object sendSuccessfulResponse(response.Response serviceRes, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(200);
        sparkRes.body(gson.toJson(serviceRes));
        return gson.toJson(serviceRes);
    }

    protected Object sendBadRequestResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(400);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResponse(errorMessage));
    }

    protected Object sendUnauthorizedResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(401);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResponse(errorMessage));
    }

    protected Object sendAlreadyTakenResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(403);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResponse(errorMessage));
    }
}
