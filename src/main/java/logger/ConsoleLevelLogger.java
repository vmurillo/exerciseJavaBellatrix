package logger;

import loggingCommon.LogginLevel;
import loggingCommon.exception.LoggingExcetion;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLevelLogger implements LevelLogger {
    private final Logger logger;

    public ConsoleLevelLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(LogginLevel level, String message) {
        ConsoleHandler consoleHandler = null;
        try {
            consoleHandler = new ConsoleHandler();
            logger.addHandler(consoleHandler);
            Level logginLevel = level.getLevel();
            logger.log(logginLevel, message);
        } catch (Exception e) {
            throw new LoggingExcetion(e.getMessage());
        } finally {
            if (consoleHandler != null) {
                consoleHandler.close();
            }
        }
    }
}
