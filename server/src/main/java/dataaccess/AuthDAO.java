package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken);
    void createAuth(String username);
    void deleteAuth(String authToken);
    void clearAllAuths();
}
