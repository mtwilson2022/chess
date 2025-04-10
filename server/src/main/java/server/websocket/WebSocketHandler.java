package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;
import static websocket.commands.UserGameCommand.CommandType.*;
import websocket.messages.*;
import dataaccess.UnauthorizedException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


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
    public void onMessage(Session session, String msg) throws IOException {
        try {
            var command = getCommand(msg);

            Integer gameID = command.getGameID();

            String username = getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(gameID, session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(gameID, session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(gameID, session, username, (LeaveCommand) command);
                case RESIGN -> resign(gameID, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) { // from the getUsername func
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private UserGameCommand getCommand(String msg) {
        var serializer = new Gson();
        UserGameCommand command = serializer.fromJson(msg, UserGameCommand.class); // TODO: may need to do extra work to deserialize, esp. with MakeMove
        var commandType = command.getCommandType();

        if (commandType == CONNECT) {
            return serializer.fromJson(msg, ConnectCommand.class);
        } else if (commandType == MAKE_MOVE) {
            return serializer.fromJson(msg, MakeMoveCommand.class);
        } else if (commandType == LEAVE) {
            return serializer.fromJson(msg, LeaveCommand.class);
        } else if (commandType == RESIGN) {
            return serializer.fromJson(msg, ResignCommand.class);
        } else {
            throw new RuntimeException("Command has no CommandType field.");
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return auth.username();
    }

    private void connect(Integer gameID, Session session, String username, ConnectCommand command) throws IOException, DataAccessException {
        connections.add(gameID, username, session);

        var game = getCurrentGameBoard(command.getGameID());
        var gameMsg = new LoadGameMessage(game);
        connections.broadcastToRoot(gameID, username, gameMsg);

        var notifyStr = String.format("%s has joined the game.", username); // TODO: add player/observer functionality
        var serverMsg = new NotificationMessage(notifyStr);
        connections.broadcastToOthers(gameID, username, serverMsg);
    }

    private void makeMove(Integer gameID, Session session, String username, MakeMoveCommand command) throws IOException, DataAccessException {

    }

    private void leaveGame(Integer gameID, Session session, String username, LeaveCommand command) throws IOException, DataAccessException {

    }

    private void resign(Integer gameID, String username, ResignCommand command) throws IOException, DataAccessException {

    }

    private ChessBoard getCurrentGameBoard(int gameID) throws DataAccessException {
        var chessGame = gameDAO.getGame(gameID);
        return chessGame.game().getBoard();
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) throws IOException {
        String msg = new Gson().toJson(errorMessage);
        remote.sendString(msg);
    }
}