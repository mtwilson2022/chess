package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {
    GameData getGame(String gameID);
    List<GameData> listGames(String username, String playerColor);

    void createGame(String gameName, String gameID);
    void updateGame(GameData game);
    void updateGame(String username, String playerColor);

    void deleteGame(String gameID);
    void clearAllGames();
}
