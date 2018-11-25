package logger.manager;

import logger.LevelLogger;
import loggingCommon.LogginLevel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoggerManagerTest {
    private final LevelLogger dummyLogger1 = Mockito.mock(LevelLogger.class);
    private final LevelLogger dummyLogger2 = Mockito.mock(LevelLogger.class);
    private final List<LevelLogger> dummyLoggers = Arrays.asList(dummyLogger1, dummyLogger2);
    private final Logger logger = Mockito.mock(Logger.class);
    private final LoggerManager underTest = new LoggerManager(dummyLoggers, logger);

    @Before
    public void setUp() throws Exception {
        doNothing().when(dummyLogger1).log(any(), any());
        doNothing().when(dummyLogger2).log(any(), any());
    }

    @Test
    public void log() {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel logginLevel = LogginLevel.MESSAGE;
        // then
        underTest.log(logginLevel, dummyMessage);
        // assert dummyLogger1 calls log with input parameters
        ArgumentCaptor<LogginLevel> logginLevelArgumentCaptor = ArgumentCaptor.forClass(LogginLevel.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(dummyLogger1, times(1)).log(logginLevelArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertThat(logginLevelArgumentCaptor.getValue(), is(logginLevel));
        assertThat(stringArgumentCaptor.getValue(), is(dummyMessage));
        // assert dummyLogger2 calls log with input parameters
        assertDummyLogger(dummyLogger1, dummyMessage, logginLevel);
        assertDummyLogger(dummyLogger2, dummyMessage, logginLevel);
    }

    @Test
    public void noInputLoggers() {
        try {
            new LoggerManager(Collections.emptyList(), logger);
            fail("Exception should happen");
        } catch (RuntimeException e) {
            verify(dummyLogger1, never()).log(any(), any());
            verify(dummyLogger2, never()).log(any(), any());
        }
    }

    @Test
    public void oneLoggerThrows() {
        // when
        String dummyMessage = "dummyMessage";
        LogginLevel dummyLevel = LogginLevel.WARNING;
        RuntimeException e = new RuntimeException();
        doThrow(e).when(dummyLogger2).log(any(), any());
        // then
        underTest.log(dummyLevel, dummyMessage);
        // assert logger 1 logs ok
        assertDummyLogger(dummyLogger1, dummyMessage, dummyLevel);
        verify(dummyLogger2, times(1)).log(eq(dummyLevel), eq(dummyMessage));
    }

    private static void assertDummyLogger(LevelLogger dummyLogger, String dummyMessage, LogginLevel dummyLevel) {
        ArgumentCaptor<LogginLevel> logginLevelArgumentCaptor = ArgumentCaptor.forClass(LogginLevel.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(dummyLogger, times(1)).log(logginLevelArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertThat(logginLevelArgumentCaptor.getValue(), is(dummyLevel));
        assertThat(stringArgumentCaptor.getValue(), is(dummyMessage));
    }
}