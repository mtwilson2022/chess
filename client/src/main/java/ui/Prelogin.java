package ui;

import server.ServerFacade;

public class Prelogin implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;

    public Prelogin(String url) {
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

}
