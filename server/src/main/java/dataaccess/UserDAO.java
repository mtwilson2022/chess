package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void insertUser(UserData user);
    void deleteUser(String username);
    void clearAllUsers();
}
