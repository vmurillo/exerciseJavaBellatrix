import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// There are several problems with this class:
// It is not an object because all its members are static
// LogMessage doest too many things, it checks inputs, it opens a db connection, it creates a file, etc. This violates
// the single responsibility principle.
// Resources like handlers, db connections and statements are not closed, this can leak this resources.
// No dependency inversion principle here, since there is no module and the only module hast be aware all details
// No di nor sr principles are met. It is not possible to unit test this code.
// Proposal:
// 1. Logging to console, file and db share the same behaviour. Create an abstraction for this common behaviour.
// 2. Three different implementations of the absctractions will be created (console, file, db). Each of those will be testable
// 3. All resources will be properly managed with a basic strategy, calling close after use
// Logging to console, file and db share the same behaviour.
public class JobLogger {
    /* using static members means that all instances will share the same members, this isnt a real object since there is no encapsulation. Also this class cant be used in a parallel context because of the same reasons */
    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    // no used member
    private boolean initialized;
    private static Map dbParams;
    private static Logger logger;

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                     boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
        // logger is initialized from a static class, it wont be easy to mock
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    //
    public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
        /* trim returns a copy, but it is not saved, thus leading and trailing whitespaces wont be erased*/
        messageText.trim();
        // should use isEmpty instead of length == 0
        if (messageText == null || messageText.length() == 0) {
            return;
        }
        // this check could be implemented at construction time
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");
        }
        // the same codition is tested, no need
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified");
        }

        Connection connection = null;
        Properties connectionProps = new Properties();
        // no sanity check for userNAme, password in dbParams
        connectionProps.put("user", dbParams.get("userName"));
        connectionProps.put("password", dbParams.get("password"));

        connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                + ":" + dbParams.get("portNumber") + "/", connectionProps);
        /* db connection are a delicate resource, code should handle a proper way to deal with connections (clean up, pool)
        */

        int t = 0; // whis is t for? variable names should give an idea of their use
        if (message && logMessage) {
            t = 1;
        }

        if (error && logError) {
            t = 2;
        }

        if (warning && logWarning) {
            t = 3;
        }

        Statement stmt = connection.createStatement();
        // an statement is a resource that should be handled properly, code should deal with clean up
        // create an statemtn each time this function is called is very ineficient, it may be a good idea to use a prepared statement for isntance

        String l = null;
        File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        // handlers objects are responsible of resources, after resources are used/consumed the handles provide a way to release them
        // this normally happens by calling the close method
        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
        ConsoleHandler ch = new ConsoleHandler();

        // DateFormat.getDateInstance(DateFormat.LONG) can be reused among all these calls
        // strings concatenation can be costly, specially when dealing with big strings. A string builder could handy here
        if (error && logError) {
            l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (warning && logWarning) {
            l = l + "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (message && logMessage) {
            l = l + "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if(logToFile) {
            logger.addHandler(fh);
            logger.log(Level.INFO, messageText);
        }

        if(logToConsole) {
            logger.addHandler(ch);
            logger.log(Level.INFO, messageText);
        }

        if(logToDatabase) {
            stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");
        }
    }
}
