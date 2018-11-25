package logger;

import loggingCommon.LogginLevel;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

public class ConsoleLevelLoggerTest {
    private Logger logger = Mockito.mock(Logger.class);
    private final ConsoleLevelLogger underTest = new ConsoleLevelLogger(logger);

    @Test
    public void log() {
        // when
        final String dummyText = "dummyText";
        final LogginLevel dummyLevel = LogginLevel.WARNING;
        // then
        doNothing().when(logger).addHandler(ArgumentMatchers.any(ConsoleHandler.class));
        doNothing().when(logger).log(ArgumentMatchers.any(Level.class), ArgumentMatchers.anyString());
        underTest.log(dummyLevel, dummyText);
        // assert logger calls handler
        verify(logger, times(1)).addHandler(ArgumentMatchers.any(ConsoleHandler.class));
        ArgumentCaptor<Level> levelsArgumentCaptor = ArgumentCaptor.forClass(Level.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(1)).log(levelsArgumentCaptor.capture(), messageArgumentCaptor.capture());
        List<Level> actualLevels =levelsArgumentCaptor.getAllValues();
        assertThat(actualLevels, hasSize(1));
        assertThat(actualLevels, Matchers.contains(dummyLevel.getLevel()));
    }
}