package dataaccess;

import model.GameData;

import java.util.List;
import java.util.Set;

public interface GameDAO {
    GameData getGame(int gameID);
    List<GameData> listGames();
    Set<Integer> getAllGameIDs();

    void createNewGame(String gameName, int gameID);
    void updateGame(String username, String playerColor, Integer gameID);

    void clearAllGames();
}
