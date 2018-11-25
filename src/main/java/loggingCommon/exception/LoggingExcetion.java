package loggingCommon.exception;

public class LoggingExcetion extends RuntimeException {
    public LoggingExcetion(String message) {
        super(message);
    }

    public LoggingExcetion(String message, Throwable e) {
        super(message, e);
    }
}
