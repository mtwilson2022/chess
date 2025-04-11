package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.GameData;
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
                case CONNECT -> connect(gameID, session, username);
                case MAKE_MOVE -> makeMove(gameID, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(gameID, username);
                case RESIGN -> resign(gameID, username);
            }
        } catch (UnauthorizedException ex) { // from the getUsername func
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (IllegalCommandException ex) {
            sendMessage(session.getRemote(), new ErrorMessage(ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private UserGameCommand getCommand(String msg) {
        var serializer = new Gson();
        UserGameCommand command = serializer.fromJson(msg, UserGameCommand.class);
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

    private enum ClientRole {
        WHITE_PLAYER,
        BLACK_PLAYER,
        OBSERVER
    }

    /*
    Sends LOAD_GAME back to client, and NOTIFICATION to others (including their player color / observing)
     */
    private void connect(Integer gameID, Session session, String username) throws IOException, DataAccessException {
        connections.add(gameID, username, session);

        var game = getCurrentGameBoard(gameID);
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
    private void makeMove(Integer gameID, String username, MakeMoveCommand command) throws IOException, DataAccessException, IllegalCommandException {
        var gameData = gameDAO.getGame(gameID);
        var chessGame = gameData.game();

        if (chessGame.isGameOver()) {
            throw new IllegalCommandException("Error: the game has already ended.");
        }

        // check that it's the player's turn
        checkPlayerTurn(getRole(gameData, username), chessGame.getTeamTurn());

        var move = command.getMove();
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new IllegalCommandException("Error: that move is invalid.");
        }

        var gson = new Gson();
        gameDAO.updateChessGame(gameID, gson.toJson(chessGame));

        var board = chessGame.getBoard();
        var gameMsg = new LoadGameMessage(board);
        connections.broadcastToAll(gameID, gameMsg);

        var moveStr = String.format("%s has moved a piece: [move]", username);
        var moveMsg = new NotificationMessage(moveStr);
        connections.broadcastToOthers(gameID, username, moveMsg);

        sendCheckMessages(gameID, username, chessGame);
    }

    private void checkPlayerTurn(ClientRole role, ChessGame.TeamColor playerTurn) {
        if (role == ClientRole.OBSERVER) {
            throw new IllegalCommandException("Error: observers may not make moves.");
        } else if (role == ClientRole.WHITE_PLAYER && playerTurn == ChessGame.TeamColor.BLACK) {
            throw new IllegalCommandException("Error: it is the other player's turn.");
        } else if (role == ClientRole.BLACK_PLAYER && playerTurn == ChessGame.TeamColor.WHITE) {
            throw new IllegalCommandException("Error: it is the other player's turn.");
        }
    }

    private void sendCheckMessages(Integer gameID, String username, ChessGame chessGame) throws IOException, DataAccessException {
        var gson = new Gson();
        ChessGame.TeamColor enemyColor = chessGame.getTeamTurn();

        if (chessGame.isInCheckmate(enemyColor)) {
            chessGame.markGameAsOver();
            gameDAO.updateChessGame(gameID, gson.toJson(chessGame));
            var mateStr = String.format("The game ends in checkmate. %s wins!", username); // TODO: say which player is in checkmate
            var mateMsg = new NotificationMessage(mateStr);
            connections.broadcastToAll(gameID, mateMsg);

        } else if (chessGame.isInCheck(enemyColor)) {
            var checkStr = String.format("%s has moved a piece: [move]", username);
            var checkMsg = new NotificationMessage(checkStr);
            connections.broadcastToAll(gameID, checkMsg);

        } else if (chessGame.isInStalemate(enemyColor)) {
            chessGame.markGameAsOver();
            gameDAO.updateChessGame(gameID, gson.toJson(chessGame));
            var staleMsg = new NotificationMessage("The game ends in stalemate.");
            connections.broadcastToAll(gameID, staleMsg);
        }
    }

    /*
    Player / observer's connection is removed; a message is broadcast to the other clients.
    If playing the game, they are also cleared from the database.
     */
    private void leaveGame(Integer gameID, String username) throws IOException, DataAccessException {
        var gameData = gameDAO.getGame(gameID);
        var role = getRole(gameData, username);

        if (role == ClientRole.WHITE_PLAYER) {
            gameDAO.updateGame(null, "white", gameID);
        } else if (role == ClientRole.BLACK_PLAYER) {
            gameDAO.updateGame(null, "black", gameID);
        }

        connections.remove(gameID, username);

        var notifyStr = String.format("%s has left the game.", username);
        var serverMsg = new NotificationMessage(notifyStr);
        connections.broadcastToOthers(gameID, username, serverMsg);
    }

    /*
    Mark the game as over. Once a game is over, no one may resign or make a move.
    Send NOTIFICATION to all
     */
    private void resign(Integer gameID, String username) throws IOException, DataAccessException, IllegalCommandException {
        var gameData = gameDAO.getGame(gameID);
        var role = getRole(gameData, username);
        var chessGame = gameData.game();

        if (chessGame.isGameOver()) {
            throw new IllegalCommandException("Error: the game has already ended.");
        }

        if (role == ClientRole.WHITE_PLAYER || role == ClientRole.BLACK_PLAYER) {
            chessGame.markGameAsOver();
            gameDAO.updateChessGame(gameID, new Gson().toJson(chessGame));

            var notifyStr = String.format("%s has resigned.", username);
            var serverMsg = new NotificationMessage(notifyStr);
            connections.broadcastToAll(gameID, serverMsg);

        } else {
            throw new IllegalCommandException("Error: observers may not resign from the game.");
        }
    }

    private ClientRole getRole(GameData game, String username) {
        if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            return ClientRole.WHITE_PLAYER;
        } else if (game.blackUsername() != null && game.blackUsername().equals(username)) {
            return ClientRole.BLACK_PLAYER;
        } else {
            return ClientRole.OBSERVER;
        }
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