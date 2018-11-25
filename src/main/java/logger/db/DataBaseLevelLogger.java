package logger.db;

import logger.LevelLogger;
import loggingCommon.LogginLevel;
import loggingCommon.db.DataBaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseLevelLogger implements LevelLogger {
    private final DataBaseConnectionFactory dataBaseConnectionFactory;
    protected final static String QUERY_TEMPLATE = "insert into Log_Values (level,message) values (?, ?)";

    public DataBaseLevelLogger(DataBaseConnectionFactory dataBaseConnectionFactory) {
        this.dataBaseConnectionFactory = dataBaseConnectionFactory;
    }

    @Override
    public void log(LogginLevel level, String message) {
        try (Connection connection = dataBaseConnectionFactory.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE)) {
                statement.setString(1, level.name());
                statement.setString(2, message);
                statement.execute();
            }
        } catch (SQLException e) {
            String error = "Error when logging into a DB";
            throw new RuntimeException(error, e.getCause());
        }
    }
}
