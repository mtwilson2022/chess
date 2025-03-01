package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemGameDAO implements GameDAO {

    private Map<Integer, GameData> games;  // maps gameIDs to their GameData objects

    public MemGameDAO() {
        this.games = new HashMap<>();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void createNewGame(String gameName, int gameID) {
        var game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
    }

    @Override
    public void updateGame(String username, String playerColor, GameData game) {
        GameData updatedGame = game.updateGame(username, playerColor);
        games.put(updatedGame.gameID(), updatedGame);
    }

    @Override
    public void deleteGame(int gameID) {
        games.remove(gameID);
    }

    @Override
    public void clearAllGames() {
        games.clear();
    }
}
