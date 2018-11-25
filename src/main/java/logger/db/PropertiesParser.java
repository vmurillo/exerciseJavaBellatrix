package logger.db;

import loggingCommon.db.DataBaseConnectionParameters;

import java.util.*;
import java.util.stream.Collectors;

public class PropertiesParser {
    private final Map<String, String> properties;
    protected final static String URI_TEMPLATE = "jdbc:{}://{}:{}/";
    private final static List<String> NEEDED_PARAMETERS = Arrays.asList(
            "userName",
            "password",
            "dbms",
            "serverName",
            "portNumber");

    public PropertiesParser(Map<String, String> properties) throws MissingFormatArgumentException {
        boolean containsAllParameters = NEEDED_PARAMETERS
                .stream().map(param -> properties.containsKey(param))
                .reduce(true, (a, b) -> a && b);
        if (containsAllParameters) {
            this.properties = properties;
        } else {
            String mapAsStr = properties.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + entry.getValue())
                    .collect(Collectors.joining(","));
            String errorMessage = String.format("Missing one or more parameters in input properties {}", mapAsStr);
            throw new MissingFormatArgumentException(errorMessage);
        }
    }

    public DataBaseConnectionParameters getJDBCUri() {
        final Properties dbProperties = getProperties();
        final String dbUri = getUri();
        DataBaseConnectionParameters dataBaseConnectionParameters = new DataBaseConnectionParameters(dbProperties, dbUri);
        return dataBaseConnectionParameters;
    }

    private Properties getProperties() {
        final String user = properties.get("userName");
        final String password = properties.get("password");
        final Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        return properties;
    }

    private String getUri() {
        final String dbms = properties.get("dbms");
        final String serverName = properties.get("serverName");
        final String portNumber = properties.get("portNumber");
        return String.format(URI_TEMPLATE, dbms, serverName, portNumber);
    }
}
