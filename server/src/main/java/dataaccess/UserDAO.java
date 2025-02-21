package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser();
    void deleteUser();
    void clearAllUsers();
}

