package dataaccess;

import model.GameData;

import java.util.List;
import java.util.Set;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    Set<Integer> getAllGameIDs() throws DataAccessException;

    void createNewGame(String gameName, int gameID) throws DataAccessException;
    void updateGame(String username, String playerColor, Integer gameID) throws DataAccessException;

    void clearAllGames() throws DataAccessException;

    /*
    Phase 6 functions. Only needed to be implemented for SQL DAO
     */
    default void updateChessGame(Integer gameID, String gameJson) throws DataAccessException {
    }
}
