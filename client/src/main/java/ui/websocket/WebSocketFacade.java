package ui.websocket;

import com.google.gson.Gson;
import server.ResponseException; // TODO: may need to move to shared?
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ServerMessage;
import websocket.ServerMessageObserver;
import chess.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class); // TODO: deserialize to the right subclass
                    observer.notify(msg);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    /*
    Redraws the board upon the user's request.
     */
    public void redrawBoard() throws ResponseException {
        // let's do this later.
        // may use http rather than ws? If so, then move this out of WSF
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException {
        try {
            var cmd = new LeaveCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var cmd = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var cmd = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
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