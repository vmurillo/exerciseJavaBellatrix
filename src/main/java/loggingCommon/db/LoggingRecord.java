package loggingCommon.db;

public class LoggingRecord {
    private final static String MESSAGE_TEMPLATE = "{} {} {}";
    private final String loggingLevel;
    private final String timeStamp;
    private final String message;

    public LoggingRecord(String loggingLevel, String timeStamp, String message) {
        this.loggingLevel = loggingLevel;
        this.timeStamp = timeStamp;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format(MESSAGE_TEMPLATE, loggingLevel, timeStamp, message);
    }
}
