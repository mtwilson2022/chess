package dataaccess;

import model.GameData;

import java.util.List;
import java.util.Set;

public class SqlGameDAO extends SqlDataAccess implements GameDAO {

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public Set<Integer> getAllGameIDs() {
        return Set.of();
    }

    @Override
    public void createNewGame(String gameName, int gameID) {

    }

    @Override
    public void updateGame(String username, String playerColor, Integer gameID) {

    }

    @Override
    public void clearAllGames() {

    }
}
