package ui;

import server.ResponseException;
import server.ServerFacade;

import static ui.State.*;

public class Gameplay implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server;

    public Gameplay(String url) {
        server = new ServerFacade(url);
    }

    @Override
    public State help() {
        return GAMEPLAY;
    }

    @Override
    public State eval(String input) throws ResponseException {
        return null;
    }

    /*
    Nothing else will be done here until Phase 6.
     */

}
