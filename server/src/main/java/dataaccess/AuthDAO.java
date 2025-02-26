package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken);
    void insertAuth(AuthData auth);
    void deleteAuth(String authToken);
    void clearAllAuths();
}
