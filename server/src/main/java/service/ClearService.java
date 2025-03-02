package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.ClearRequest;
import response.ClearResponse;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResponse clear(ClearRequest ignoredReq) {
        clearUsers();
        clearGames();
        clearAuths();
        return new ClearResponse();
    }

    private void clearUsers() {
        this.userDAO.clearAllUsers();
    }

    private void clearGames() {
        this.gameDAO.clearAllGames();
    }

    private void clearAuths() {
        this.authDAO.clearAllAuths();
    }
}
