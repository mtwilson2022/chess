package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData updateGame(String username, String playerColor) throws IllegalArgumentException {
        if (playerColor.equalsIgnoreCase("white")) {
            return new GameData(gameID, username, blackUsername, gameName, game);
        } else if (playerColor.equalsIgnoreCase("black")) {
            return new GameData(gameID, whiteUsername, username, gameName, game);
        } else {
            throw new IllegalArgumentException("Invalid player color.");
        }
    }
}
