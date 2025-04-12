package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import server.ResponseException;
import server.ServerFacade;
import ui.websocket.WebSocketFacade;
import websocket.ServerMessageObserver;
import websocket.commands.*;
import websocket.messages.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

import java.io.IOException;

import static ui.State.*;
import static websocket.commands.UserGameCommand.CommandType.*;

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
            public void notify(String message) {
                var serverMessage = getMessage(message);

                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> printLoadGame((LoadGameMessage) serverMessage);
                    case ERROR -> printError((ErrorMessage) serverMessage);
                    case NOTIFICATION -> printNotification((NotificationMessage) serverMessage);
                }
            }
        });
    }

    private ServerMessage getMessage(String msg) {
        var serializer = new Gson();
        ServerMessage message = new Gson().fromJson(msg, ServerMessage.class);
        var messageType = message.getServerMessageType();

        if (messageType == LOAD_GAME) {
            return serializer.fromJson(msg, LoadGameMessage.class);
        } else if (messageType == ERROR) {
            return serializer.fromJson(msg, ErrorMessage.class);
        } else if (messageType == NOTIFICATION) {
            return serializer.fromJson(msg, NotificationMessage.class);
        } else {
            throw new RuntimeException("Message has no ServerMessageType field.");
        }
    }

    private void printLoadGame(LoadGameMessage message) {

    }

    private void printError(ErrorMessage message) {

    }

    private void printNotification(NotificationMessage message) {

    }

    @Override
    public State help() {
        return GAMEPLAY;
    }

    @Override
    public State eval(String input) throws ResponseException {
        return null;
    }

    /**
     * This method is called in the transition from the postlogin to gameplay UI.
     * It establishes a user's WebSocket connection and alerts other users.
     * @throws ResponseException if something bad happens
     */
    public void connectGame() throws ResponseException {
        ws.connect(authToken, gameID);
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
        ws.leaveGame(authToken, gameID);
    }

    /*
    Allow the user to input what move they want to make. The board is updated to reflect the result of the move,
    and the board automatically updates on all clients involved in the game.
     */
    private void makeMove() throws ResponseException {
        // TODO: do clienty stuff that other clients do
//        var move = new ChessMove();
//        ws.makeMove(authToken, gameID, move);
    }

    /*
    Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over.
    Does not cause the user to leave the game.
     */
    private void resign() throws ResponseException {
        // TODO: "are you sure you want to do this?"

        ws.resign(authToken, gameID);
    }

    /*
    Allows the user to input the piece for which they want to highlight legal moves.
    The selected piece’s current square and all squares it can legally move to are highlighted.
    This is a local operation and has no effect on remote users’ screens.
     */
    private void highlightLegalMoves(ChessPosition position) throws ResponseException {
        // let's do this later.
        // may use http rather than ws? If so, take this out of the WSF
    }
}
