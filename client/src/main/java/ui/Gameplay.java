package ui;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import server.ResponseException;
import server.ServerFacade;
import ui.websocket.WebSocketFacade;
import websocket.ServerMessageObserver;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static ui.State.*;

public class Gameplay implements Client {
    // Phase 6: (?) add a private final String serverURL and initialize in the constructor (for WebSocket)
    private final ServerFacade server; // not sure if this will be necessary; we'll see
    private final WebSocketFacade ws;
    private final String authToken;
    private final int gameID;

    public Gameplay(String url, String auth, Integer id) throws ResponseException {
        authToken = auth;
        gameID = id;
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
    Redraws the board upon the user's request.
     */
    private void redrawBoard() throws ResponseException {
        // let's do this later.
        // may use http rather than ws? If so, then move this out of WSF
    }

    /*
    Removes the user from the game (whether they are playing or observing the game).
    The client transitions back to the Post-Login UI.
     */
    private void leaveGame() throws ResponseException {
        // call the WSF method in here
    }

    /*
    Allow the user to input what move they want to make. The board is updated to reflect the result of the move,
    and the board automatically updates on all clients involved in the game.
     */
    public void makeMove(ChessMove move) throws ResponseException {

    }

    /*
    Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over.
    Does not cause the user to leave the game.
     */
    public void resign() throws ResponseException {

    }

    /*
    Allows the user to input the piece for which they want to highlight legal moves.
    The selected piece’s current square and all squares it can legally move to are highlighted.
    This is a local operation and has no effect on remote users’ screens.
     */
    public void highlightLegalMoves(ChessPosition position) throws ResponseException {
        // let's do this later.
        // may use http rather than ws? If so, take this out of the WSF
    }
}
