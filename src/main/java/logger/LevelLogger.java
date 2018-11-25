package logger;

import loggingCommon.LogginLevel;

public interface LevelLogger {
    void log(LogginLevel level, String message);
}
