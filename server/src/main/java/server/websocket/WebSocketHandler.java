package server.websocket;

import chess.ChessBoard;
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
        } catch (IllegalCommandException ex) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: that action may not be performed"));
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

    /*
    Sends LOAD_GAME back to client, and NOTIFICATION to others (including their player color / observing)
     */
    private void connect(Integer gameID, Session session, String username, ConnectCommand command) throws IOException, DataAccessException {
        connections.add(gameID, username, session);

        var game = getCurrentGameBoard(command.getGameID());
        var gameMsg = new LoadGameMessage(game);
        connections.broadcastToRoot(gameID, username, gameMsg);

        var notifyStr = String.format("%s has joined the game.", username); // TODO: add player/observer functionality
        var serverMsg = new NotificationMessage(notifyStr);
        connections.broadcastToOthers(gameID, username, serverMsg);
    }

    /*
    Needs to do the following:
     - verify the move is valid
     - make the move and update the game
     - send LOAD_GAME to all
     - send NOTIFICATION to others
     - if move leads to check, checkmate, or stalemate, send NOTIFICATION to all
    Once a game is over, no one may resign or make a move.
     */
    private void makeMove(Integer gameID, Session session, String username, MakeMoveCommand command) throws IOException, DataAccessException, IllegalCommandException {

    }

    /*
    Player / observer's connection is removed; a message is broadcast to the other clients.
    If playing the game, they are also cleared from the database.
     */
    private void leaveGame(Integer gameID, Session session, String username, LeaveCommand command) throws IOException, DataAccessException {

    }

    /*
    Mark the game as over. Once a game is over, no one may resign or make a move.
    Send NOTIFICATION to all
     */
    private void resign(Integer gameID, String username, ResignCommand command) throws IOException, DataAccessException, IllegalCommandException {

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