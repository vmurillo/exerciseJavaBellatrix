package loggingCommon;

import java.util.logging.Level;

public enum LogginLevel {
    MESSAGE(Level.INFO),
    WARNING(Level.WARNING),
    ERROR(Level.SEVERE);

    private final Level level;

    private LogginLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
