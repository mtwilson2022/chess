package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.ClearRequest;
import result.ClearResult;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear(ClearRequest ignoredReq) throws DataAccessException {
        clearUsers();
        clearGames();
        clearAuths();
        return new ClearResult();
    }

    private void clearUsers() throws DataAccessException {
        this.userDAO.clearAllUsers();
    }

    private void clearGames() throws DataAccessException {
        this.gameDAO.clearAllGames();
    }

    private void clearAuths() throws DataAccessException {
        this.authDAO.clearAllAuths();
    }
}
