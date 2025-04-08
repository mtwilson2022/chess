package ui;

import server.ResponseException;
import server.ServerFacade;
import ui.websocket.WebSocketFacade;
import websocket.ServerMessageObserver;
import websocket.messages.ServerMessage;

import static ui.State.*;

public class Gameplay implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server; // not sure if this will be necessary; we'll see
    private final WebSocketFacade ws;

    public Gameplay(String url) throws ResponseException {
        server = new ServerFacade(url);
        ws = new WebSocketFacade(url, new ServerMessageObserver() {
            @Override
            public void notify(ServerMessage message) {
                // TODO: put stuff here
            }
        });
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
