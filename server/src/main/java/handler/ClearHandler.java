package handler;

import dataaccess.DataAccessException;
import request.ClearRequest;
import result.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class ClearHandler extends HttpHandler {
    private final ClearService service;
    private final Gson gson;

    public ClearHandler(ClearService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public Object clear(Request req, Response res) throws DataAccessException {
        ClearRequest clearReq = gson.fromJson(req.body(), ClearRequest.class);
        ClearResult clearRes = service.clear(clearReq);
        return sendSuccessfulResponse(clearRes, res, gson);
    }
}
