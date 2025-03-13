package service;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import model.*;
import java.util.UUID;
import request.*;
import result.*;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        UserData user = userDAO.getUser(req.username());

        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (!BCrypt.checkpw(req.password(), user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, user.username());
        authDAO.insertAuth(auth);

        return new LoginResult(user.username(), token);
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException, BadRequestException {
        // verify the input is correct
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(req.username());
        if (user != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        var hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        var newUser = new UserData(req.username(), hashedPassword, req.email());
        userDAO.insertUser(newUser);

        var token = UUID.randomUUID().toString();
        var newAuth = new AuthData(token, newUser.username());
        authDAO.insertAuth(newAuth);

        return new RegisterResult(newUser.username(), token);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
        var token = req.authToken();

        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(token);

        return new LogoutResult();
    }

}
