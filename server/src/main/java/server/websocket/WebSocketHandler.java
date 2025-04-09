package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;
import websocket.messages.*;
import dataaccess.UnauthorizedException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        connections = new ConnectionManager();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            var serializer = new Gson();
            UserGameCommand command = serializer.fromJson(msg, UserGameCommand.class); // TODO: may need to do extra work to deserialize, esp. with MakeMove

            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session); // get it into the connection manager

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command); // these methods should send server messages
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) { // from the getUsername func
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return auth.username();
    }

    private void saveSession(Integer gameID, Session session) {
        // because connections is a Map<String, Connection> we need to turn the gameID into a string
        String id = gameID.toString();
        connections.add(id, session);
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        connections.add(username, session);
        var message = String.format("%s has joined the game.", username);
        var serverMsg = new NotificationMessage(message);
        connections.broadcastToOthers(username, serverMsg);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

    }

    private void leaveGame(Session session, String username, LeaveCommand command) {

    }

    private void resign(Session session, String username, ResignCommand command) {

    }


    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) {
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

}