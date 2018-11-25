package logger.db;

import loggingCommon.db.DataBaseConnectionParameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PropertiesParserTest {
    private final String dummyUserName = "dummyUserName";
    private final String dummyPassWord = "dummyPassWord";
    private final String dummyDbms = "dummyDbms";
    private final String dummyServerName = "dummyServerName";
    private final String dummyPortNumber = "dummyPortNumber";

    @Test
    public void getJDBCUri() {
        // when
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userName", dummyUserName);
        parameters.put("password", dummyPassWord);
        parameters.put("dbms", dummyDbms);
        parameters.put("serverName", dummyServerName);
        parameters.put("portNumber", dummyPortNumber);
        PropertiesParser propertiesParser = new PropertiesParser(parameters);
        // then
        DataBaseConnectionParameters dbParameters = propertiesParser.getJDBCUri();
        // assert uri
        assertThat(dbParameters.getJdbcUri(), is(String.format(PropertiesParser.URI_TEMPLATE, dummyDbms, dummyServerName, dummyPortNumber)));
        // assert properties
        assertThat(dbParameters.getProperties(), hasEntry("user", dummyUserName));
        assertThat(dbParameters.getProperties(), hasEntry("password", dummyPassWord));
    }

    @Test
    public void incompleteInputMap() {
        // when
        Map<String, String> parameters = new HashMap<>();
        try {
            new PropertiesParser(parameters);
            fail("Exception should happen");
        } catch (MissingFormatArgumentException e) {
            assertThat(e, isA(MissingFormatArgumentException.class));
        }
    }
}