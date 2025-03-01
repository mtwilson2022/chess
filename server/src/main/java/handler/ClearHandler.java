package handler;

import request.ClearRequest;
import response.ClearResponse;
import service.ClearService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;


public class ClearHandler {
    private final ClearService service;
    private final Gson gson;

    public ClearHandler(ClearService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public Object clear(Request req, Response res) {
        ClearRequest clearReq = gson.fromJson(req.body(), ClearRequest.class);
        var clearRes = service.clear(clearReq);
        return gson.toJson(clearRes);
    }
}
