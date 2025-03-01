package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class GameService {

    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

}
