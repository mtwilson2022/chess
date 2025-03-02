package handler;

import com.google.gson.Gson;
import response.FailResponse;

public class HttpHandler {
    protected Object sendSuccessfulResponse(response.Response serviceRes, spark.Response sparkRes, Gson gson) {
        sparkRes.body(gson.toJson(serviceRes));
        sparkRes.status(200);
        return gson.toJson(serviceRes);
    }

    protected Object sendBadRequestResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.body(errorMessage);
        sparkRes.status(400);
        return gson.toJson(new FailResponse(errorMessage));
    }

    protected Object sendUnauthorizedResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.body(errorMessage);
        sparkRes.status(401);
        return gson.toJson(new FailResponse(errorMessage));
    }

    protected Object sendAlreadyTakenResponse(String errorMessage, spark.Response sparkRes, Gson gson) {
        sparkRes.body(errorMessage);
        sparkRes.status(403);
        return gson.toJson(new FailResponse(errorMessage));
    }
}
