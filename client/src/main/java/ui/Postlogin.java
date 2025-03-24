package ui;

import server.ServerFacade;

public class Postlogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;
    private final String authToken;

    public Postlogin(String url, String auth) {
        server = new ServerFacade(url);
        authToken = auth;
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String eval(String input) {
        return "";
    }

    private String logout() {
        return "Logged out successfully.";
    }

    private String createGame() {
        return "";
    }

    private String listGames() {
        return "";
    }

    private String playGame() {
        // phase 5: draw board from white/black's perspective
        return "";
    }

    private String observeGame() {
        // phase 5: draw board from White's perspective
        return "";
    }

}
