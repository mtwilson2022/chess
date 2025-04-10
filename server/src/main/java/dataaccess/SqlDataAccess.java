package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class SqlDataAccess {

    public SqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    enum TableType {
        USER,
        AUTH,
        GAME
    }

    /*
    Statements to initialize and configure the SQL tables.
    SQL notes:
        primary keys are indexed by default
     */
    private static final String[] STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            gameJson TEXT NOT NULL,
            PRIMARY KEY (gameID)
            );
            """
    };

    private static final Map<TableType, String> MAKE_TABLE_STATEMENTS =
            Map.of(TableType.USER, STATEMENTS[0], TableType.AUTH, STATEMENTS[1], TableType.GAME, STATEMENTS[2]);

    /**
     * Creates the tables needed for the chess database if they do not already exist.
     */
    static void createTable(TableType table) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(MAKE_TABLE_STATEMENTS.get(table))) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
