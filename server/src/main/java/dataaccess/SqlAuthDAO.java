package dataaccess;

import model.AuthData;

public class SqlAuthDAO extends SqlDataAccess implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        SqlDataAccess.createTable(TableType.AUTH);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void insertAuth(AuthData auth) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clearAllAuths() {

    }
}
