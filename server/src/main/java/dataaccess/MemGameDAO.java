package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemGameDAO implements GameDAO {

    private final Map<Integer, GameData> games;  // maps gameIDs to their GameData objects

    public MemGameDAO() {
        this.games = new HashMap<>();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() {
        var gameCollection = games.values();
        return new ArrayList<>(gameCollection);
    }

    @Override
    public Set<Integer> getAllGameIDs() {
        return games.keySet();
    }

    @Override
    public void createNewGame(String gameName, int gameID) {
        var game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
    }

    @Override
    public void updateGame(String username, String playerColor, Integer gameID) {
        GameData updatedGame = games.get(gameID).update(username, playerColor);
        games.put(updatedGame.gameID(), updatedGame);
    }

    @Override
    public void clearAllGames() {
        games.clear();
    }
}
