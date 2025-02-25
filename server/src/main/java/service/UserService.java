package service;

import dataaccess.MemUserDAO;
import request.*;
import response.*;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.*;
import java.util.UUID;

public class UserService {

    public LoginResponse login(LoginRequest req) {
        UserDAO userDAO = new MemUserDAO(); // make everything in the DAO static??
        UserData user = userDAO.getUser(req.username());

        // assert user.password() == req.password();

        var auth = UUID.randomUUID().toString();

        // create new authData and store it using the DAO

        var resp = new LoginResponse(user.username(), auth);
        return resp;
    }

}
