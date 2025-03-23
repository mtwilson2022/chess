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

}
