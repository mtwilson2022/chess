package service;

import dataaccess.AlreadyTakenException;
import dataaccess.UnauthorizedException;
import request.*;
import response.*;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.*;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResponse login(LoginRequest req) throws UnauthorizedException {
        UserData user = userDAO.getUser(req.username());

        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (!user.password().equals(req.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, user.username());
        authDAO.insertAuth(auth);

        return new LoginResponse(user.username(), token);
    }

    public RegisterResponse register(RegisterRequest req) throws AlreadyTakenException, BadRequestException {
        // verify the input is correct
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(req.username());
        if (user != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        var newUser = new UserData(req.username(), req.password(), req.email());
        userDAO.insertUser(newUser);

        var token = UUID.randomUUID().toString();
        var newAuth = new AuthData(token, newUser.username());
        authDAO.insertAuth(newAuth);

        return new RegisterResponse(newUser.username(), token);
    }

    public LogoutResponse logout(LogoutRequest req) throws UnauthorizedException {
        var token = req.authToken();

        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(token);

        return new LogoutResponse();
    }

}
