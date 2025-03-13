package handler;

import com.google.gson.Gson;
import result.FailResult;
import result.Result;

public class HttpHandler {
    protected Object sendSuccessfulResponse(Result serviceRes, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(200);
        sparkRes.body(gson.toJson(serviceRes));
        return gson.toJson(serviceRes);
    }

    protected Object sendBadRequestResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(400);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResult(errorMessage));
    }

    protected Object sendUnauthorizedResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(401);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResult(errorMessage));
    }

    protected Object sendAlreadyTakenResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.type("application/json");
        sparkRes.status(403);
        sparkRes.body(errorMessage);
        return gson.toJson(new FailResult(errorMessage));
    }
}
