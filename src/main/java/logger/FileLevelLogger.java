package logger;

import loggingCommon.LogginLevel;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLevelLogger implements LevelLogger {
    private final String file;
    private final Logger logger;
    public FileLevelLogger(String file, Logger logger) {
        this.file = file;
        this.logger = logger;
    }

    @Override
    public void log(LogginLevel level, String message) {
        FileHandler fileHandler = null;
        try  {
            fileHandler = new FileHandler(file);
            logger.addHandler(fileHandler);
            Level logLevel = level.getLevel();
            logger.log(logLevel, message);
        } catch (IOException e) {
            String error = String.format("Exception when creating a file handler for {}", file);
            throw new RuntimeException(error);
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
            }
        }
    }
}
