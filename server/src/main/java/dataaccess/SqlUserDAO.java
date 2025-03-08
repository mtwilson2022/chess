package dataaccess;

import model.UserData;

public class SqlUserDAO extends SqlDataAccess implements UserDAO {

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void insertUser(UserData user) {

    }

    @Override
    public void clearAllUsers() {

    }
}
