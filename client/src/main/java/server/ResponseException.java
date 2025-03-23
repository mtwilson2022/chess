package server;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResponseException extends Exception {
    private final int code;

    public ResponseException(int statusCode, String message) {
        super(message);
        code = statusCode;
    }

    public static ResponseException throwFromJson(InputStream in) {
        var map = new Gson().fromJson(new InputStreamReader(in), HashMap.class);
        int status;
        if (map.get("status") != null) {
            status = ((Double) map.get("status")).intValue();
        } else {
            status = 500;
        }
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int getStatusCode() {
        return code;
    }
}
