package loggingCommon.db;

import java.text.DateFormat;
import java.util.Date;

public class DataBaseStringFormatter {
    private final static String MESSAGE_TEMPLATE = "{} {} {}";
    private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG);

    public String format(String levelName, String message) {
        Date now = new Date();
        String timeStamp = DATE_FORMAT.format(now);
        return String.format(MESSAGE_TEMPLATE, levelName, timeStamp, message);
    }
}
