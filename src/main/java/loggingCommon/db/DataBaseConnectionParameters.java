package loggingCommon.db;

import java.util.Properties;

public class DataBaseConnectionParameters {
    private final Properties properties;
    private final String jdbcUri;

    public DataBaseConnectionParameters(Properties properties, String jdbcUri) {
        this.properties = properties;
        this.jdbcUri = jdbcUri;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getJdbcUri() {
        return jdbcUri;
    }
}
