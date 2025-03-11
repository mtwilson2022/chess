package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.*;
import model.*;
import request.*;
import response.ClearResponse;

public class ClearServiceTests {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    ClearService service;

    @BeforeEach
    public void setUp() {
        try {
            this.userDAO = new MemUserDAO(); // change to SQLUserDAO in phase 4?
            this.gameDAO = new MemGameDAO(); // change to SQLAuthDAO in phase 4?
            this.authDAO = new MemAuthDAO(); // change to SQLAuthDAO in phase 4?
            this.service = new ClearService(userDAO, gameDAO, authDAO);

            userDAO.insertUser(new UserData("user1", "password", "email@example.com"));
            userDAO.insertUser(new UserData("user2", "word", "another_email"));
            userDAO.insertUser(new UserData("user3", "12345678", "yet_another_email"));
            gameDAO.createNewGame("new game", 123);
            gameDAO.createNewGame("another new game", 4444);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testClear() {
        try {
            Assertions.assertInstanceOf(ClearResponse.class, service.clear(new ClearRequest()));
            Assertions.assertEquals(0, gameDAO.listGames().size());
            Assertions.assertNull(userDAO.getUser("user1"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
