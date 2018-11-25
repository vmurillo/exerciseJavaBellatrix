package loggingCommon.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnectionFactory {
    private final DataBaseConnectionParameters parameters;

    public DataBaseConnectionFactory(DataBaseConnectionParameters parameters) {
        this.parameters = parameters;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(parameters.getJdbcUri(), parameters.getProperties());
    }
}
