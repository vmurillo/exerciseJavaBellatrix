package logger.db;

import loggingCommon.LogginLevel;
import loggingCommon.db.DataBaseConnectionFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class DataBaseLevelLoggerTest {
    private final DataBaseConnectionFactory connectionFactory = Mockito.mock(DataBaseConnectionFactory.class);
    private final DataBaseLevelLogger underTest = new DataBaseLevelLogger(connectionFactory);
    private final Connection connection = Mockito.mock(Connection.class);
    private final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);

    @Before
    public void setUp() throws Exception {
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(ArgumentMatchers.anyString())).thenReturn(preparedStatement);
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
    }

    @Test
    public void log() throws SQLException {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel dummyLevel = LogginLevel.WARNING;
        // then
        underTest.log(dummyLevel, dummyMessage);
        // assert factory is called
        verify(connectionFactory, times(1)).getConnection();
        // assert query is called
        ArgumentCaptor<String> queryCapture = ArgumentCaptor.forClass(String.class);
        verify(connection, times(1)).prepareStatement(queryCapture.capture());
        assertThat(queryCapture.getValue(), is(DataBaseLevelLogger.QUERY_TEMPLATE));
        // assert statement binds inputs
        ArgumentCaptor<String> statementInputCaptor = ArgumentCaptor.forClass(String.class);
        verify(preparedStatement, times(2)).setString(anyInt(), statementInputCaptor.capture());
        List<String> actualStatmentInputs = statementInputCaptor.getAllValues();
        assertThat(actualStatmentInputs, hasSize(2));
        assertThat(actualStatmentInputs, Matchers.contains(dummyLevel.name(), dummyMessage));
        // assert statement is executed
        verify(preparedStatement, times(1)).execute();
    }

    @Test
    public void connectionFactoryThrows() throws SQLException {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel dummyLevel = LogginLevel.WARNING;
        SQLException exception = new SQLException("sa");
        when(connectionFactory.getConnection()).thenThrow(exception);
        // then
        try {
            underTest.log(dummyLevel, dummyMessage);
            fail("Exception should happen");
        } catch (RuntimeException e) {
            // assert
            verify(connectionFactory, times(1)).getConnection();
            verify(connection, never()).prepareStatement(anyString());
            verify(preparedStatement, never()).setString(anyInt(), anyString());
            verify(preparedStatement, never()).execute();
        }
    }

    @Test
    public void connectionThrows() throws SQLException {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel dummyLevel = LogginLevel.WARNING;
        SQLException exception = new SQLException("sa");
        when(connection.prepareStatement(anyString())).thenThrow(exception);
        // then
        try {
            underTest.log(dummyLevel, dummyMessage);
            fail("Exception should happen");
        } catch (RuntimeException e) {
            // assert
            verify(connectionFactory, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, never()).setString(anyInt(), anyString());
            verify(preparedStatement, never()).execute();
        }
    }

    @Test
    public void statemenThrows() throws SQLException {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel dummyLevel = LogginLevel.WARNING;
        SQLException exception = new SQLException("sa");
        when(preparedStatement.execute()).thenThrow(exception);
        // then
        try {
            underTest.log(dummyLevel, dummyMessage);
            fail("Exception should happen");
        } catch (RuntimeException e) {
            // assert
            verify(connectionFactory, times(1)).getConnection();
            verify(connection, times(1)).prepareStatement(anyString());
            verify(preparedStatement, times(2)).setString(anyInt(), anyString());
            verify(preparedStatement, times(1)).execute();
        }
    }
}