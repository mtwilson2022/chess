package dataaccess;

import model.UserData;
import java.util.Map;
import java.util.HashMap;

public class MemUserDAO implements UserDAO {

    private Map<String, UserData> users;  // maps usernames to their UserData objects

    public MemUserDAO() {
        this.users = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void insertUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public void clearAllUsers() {
        users.clear();
    }
}
