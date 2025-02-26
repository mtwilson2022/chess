package service;

import dataaccess.MemUserDAO;
import dataaccess.UserDAO;
import dataaccess.MemAuthDAO;
import dataaccess.AuthDAO;

import model.AuthData;
import model.UserData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UserServiceTests {

    // instance vars


    @BeforeEach
    public void setUp() {
        UserDAO userDAO = new MemUserDAO(); // change to SQLUserDAO in phase 4?
        AuthDAO authDAO = new MemAuthDAO(); // change to SQLAuthDAO in phase 4?
        var service = new UserService(userDAO, authDAO);

        // add something to userDAO and authDAO
        UserData registeredUser = new UserData("user1", "password", "email@example.com");
        userDAO.insertUser(registeredUser);

        String authToken = UUID.randomUUID().toString();

        AuthData userAuth = new AuthData(authToken, "user1");
        authDAO.insertAuth(userAuth);
    }

    @Test
    public void successLogin() {

    }
}
