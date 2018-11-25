package logger;

import loggingCommon.LogginLevel;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class FileLevelLoggerTest {
    private final String file = "./dummyFilePath";
    private Logger logger = Mockito.mock(Logger.class);
    private final FileLevelLogger underTest = new FileLevelLogger(file, logger);

    @Test
    public void log() {
        // when
        final String dummyText = "dummyText";
        final LogginLevel dummyLevel = LogginLevel.MESSAGE;
        // then
        doNothing().when(logger).addHandler(ArgumentMatchers.any(FileHandler.class));
        doNothing().when(logger).log(ArgumentMatchers.any(Level.class), ArgumentMatchers.anyString());
        underTest.log(dummyLevel, dummyText);
        // assert the handler is added
        verify(logger, times(1)).addHandler(ArgumentMatchers.any(FileHandler.class));
        // assert logger has called log
        ArgumentCaptor<Level> levelsArgumentCaptor = ArgumentCaptor.forClass(Level.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(1)).log(levelsArgumentCaptor.capture(), messageArgumentCaptor.capture());
        // assert input levels are logged
        List<Level> actualLevels = levelsArgumentCaptor.getAllValues();
        assertThat(actualLevels, hasSize(1));
        assertThat(actualLevels, Matchers.contains(dummyLevel.getLevel()));
        // assert message is logged
        String actualLoggedMessage = messageArgumentCaptor.getValue();
        assertThat(actualLoggedMessage, is(dummyText));
    }
}