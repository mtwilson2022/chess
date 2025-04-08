package server.websocket;

import exception.ResponseException; // TODO: from client. is it necessary?
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand command = Serializer.fromJson(msg, UserGameCommand.class); // TODO: may need to do extra work to deserialize

            // Throws a custom UnauthorizedException. Yours may work differently.
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session); // get it into the connection manager

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command); // these methods should send server messages
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void saveSession(Integer gameID, Session session) {

    }

    private void connect(Session session, String username, ConnectCommand command) {

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

    }

    private void leaveGame(Session session, String username, LeaveCommand command) {

    }

    private void resign(Session session, String username, ResignCommand command) {

    }


    // PetShop
//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws IOException {
//        Action action = new Gson().fromJson(message, Action.class);
//        switch (action.type()) {
//            case ENTER -> enter(action.visitorName(), session);
//            case EXIT -> exit(action.visitorName());
//        }
//    }

    /*
    replace with the various command methods
     */
    private void enter(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }

}