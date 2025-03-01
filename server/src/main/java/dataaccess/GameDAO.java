package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGame(int gameID);
    Collection<GameData> listGames();

    void createNewGame(String gameName, int gameID);
    void updateGame(String username, String playerColor, GameData game);

    void deleteGame(int gameID);
    void clearAllGames();
}
