package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.*;

import model.AuthData;
import model.UserData;

import request.*;

public class UserServiceTests {

    UserDAO userDAO;
    AuthDAO authDAO;
    UserData registeredUser;
    UserService service;

    @BeforeEach
    public void setUp() {
        this.userDAO = new MemUserDAO(); // change to SQLUserDAO in phase 4?
        this.authDAO = new MemAuthDAO(); // change to SQLAuthDAO in phase 4?
        this.service = new UserService(userDAO, authDAO);

        // add something to userDAO and authDAO
        this.registeredUser = new UserData("user1", "password", "email@example.com");
        try {
            userDAO.insertUser(registeredUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void successLogin() {
        var req = new LoginRequest("user1", "password");
        try {
            Assertions.assertNotNull(service.login(req));
        } catch (UnauthorizedException e) {
            Assertions.fail("Login should not have thrown an exception.");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failLogin() {
        var requestWrongPassword = new LoginRequest("user1", "not right");
        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(requestWrongPassword));

        var requestNotAUser = new LoginRequest("not a registered user", "abc");
        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(requestNotAUser));
    }

    @Test
    public void successRegister() {
        try {
            var req = new RegisterRequest("new user", "12345678", "new_email@site");
            var resp = service.register(req);
            Assertions.assertEquals("new user", resp.username());
            Assertions.assertNotNull(authDAO.getAuth(resp.authToken()));
        } catch (BadRequestException bre) {
            throw new RuntimeException("Invalid input");
        } catch (AlreadyTakenException ate) {
            throw new RuntimeException();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void failRegister() {
        var requestAlreadyAUser = new RegisterRequest("user1", "new password", "rand_email");
        Assertions.assertThrows(AlreadyTakenException.class, () -> service.register(requestAlreadyAUser));
    }

    @Test
    public void successLogout() {
        String token = "some_token";
        var testAuthData = new AuthData(token, "user1");
        try {
            authDAO.insertAuth(testAuthData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        var req = new LogoutRequest(token);
        Assertions.assertDoesNotThrow(() -> service.logout(req));
    }

    @Test
    public void failLogout() {
        String token = "bad token";
        var req = new LogoutRequest(token);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.logout(req));
    }
}
