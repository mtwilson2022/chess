package server;

import com.google.gson.Gson;
import model.GameData;
import request.*;
import result.*;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var req = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", req, null, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws ResponseException {
        var req = new LoginRequest(username, password);
        return makeRequest("POST", "/session", req, null, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        record ListGamesResult(GameData[] games) {
        }
        var games = makeRequest("GET", "/game", null, authToken, ListGamesResult.class); // TODO: see if this works?
        return games.games();
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        record CreateGameInfo(String gameName) {
        }
        var req = new CreateGameInfo(gameName);
        var resp = makeRequest("POST", "/game", req, authToken, CreateGameResult.class);
        return resp.gameID();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        record JoinGameInfo(String playerColor, int gameID) {
        }
        var req = new JoinGameInfo(playerColor, gameID);
        makeRequest("PUT", "/game", req, authToken, null);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object requestBody, String authHeader, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("Authorization", authHeader);

            writeBody(requestBody, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            var gson = new Gson();
            String reqData = gson.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (status / 100 != 2) {
            try (InputStream errResp = http.getErrorStream()) {
                if (errResp != null) {
                    throw ResponseException.throwFromJson(errResp);
                }
            }
            throw new ResponseException(status, "Unexpected failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
