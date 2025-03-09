package dataaccess;

import model.UserData;

public class SqlUserDAO extends SqlDataAccess implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        SqlDataAccess.createTable(TableType.USER);
    }

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
