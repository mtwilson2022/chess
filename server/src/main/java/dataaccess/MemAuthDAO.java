package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO implements AuthDAO {

    private Map<String, AuthData> auths;  // maps authTokens to their AuthData objects

    public MemAuthDAO() {
        this.auths = new HashMap<>();
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clearAllAuths() {
        auths.clear();
    }
}
