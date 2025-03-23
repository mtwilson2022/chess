package ui;

import server.ServerFacade;

public class Postlogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;

    public Postlogin(String url) {
        server = new ServerFacade(url);
    }

    @Override
    public String help() {
        return "";
    }

    @Override
    public String eval(String input) {
        return "";
    }

    public String logout() {
        return "Logged out successfully.";
    }

    public String createGame() {
        return "";
    }

    public String listGames() {
        return "";
    }

    public String playGame() {
        // phase 5: draw board from white/black's perspective
        return "";
    }

    public String observeGame() {
        // phase 5: draw board from White's perspective
        return "";
    }

}
