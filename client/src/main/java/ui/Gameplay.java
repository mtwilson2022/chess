package ui;

import server.ServerFacade;

public class Gameplay implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;

    public Gameplay(String url) {
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

    /*
    Nothing else will be done here until Phase 6.
     */

}
