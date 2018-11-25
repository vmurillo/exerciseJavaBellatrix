package logger.manager;

import logger.ConsoleLevelLogger;
import logger.FileLevelLogger;
import logger.LevelLogger;
import logger.db.DataBaseLevelLogger;
import logger.db.PropertiesParser;
import loggingCommon.LogginLevel;
import loggingCommon.db.DataBaseConnectionFactory;
import loggingCommon.db.DataBaseConnectionParameters;
import loggingCommon.exception.LoggingExcetion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LoggerManager {
    private final List<LevelLogger> loggers;
    private final Logger logger;

    static public class Builder {
        private final List<LevelLogger> loggers = new ArrayList<>();
        private final Logger logger = Logger.getLogger(LoggerManager.class.getName());

        public Builder withFileLogger(String file) {
            loggers.add(new FileLevelLogger(file, logger));
            return this;
        }

        public Builder withConsoleLogger() {
            loggers.add(new ConsoleLevelLogger(logger));
            return this;
        }

        public Builder withDataBaseLogger(Map<String, String> dbParameters) {
            PropertiesParser propertiesParser = new PropertiesParser(dbParameters);
            DataBaseConnectionParameters parameters = propertiesParser.getJDBCUri();
            DataBaseConnectionFactory connectionFactory = new DataBaseConnectionFactory(parameters);
            loggers.add(new DataBaseLevelLogger(connectionFactory));
            return this;
        }

        public LoggerManager build() {
            return new LoggerManager(loggers, logger);
        }
    }

    protected LoggerManager(List<LevelLogger> loggers, Logger logger) {
        if (loggers.isEmpty()) {
            throw new RuntimeException("No logger is set");
        } else {
            this.loggers = loggers;
            this.logger = logger;
        }
    }

    public void log(LogginLevel level, String message) {
        loggers.forEach(logger -> {
            try {
                logger.log(level, message);
            } catch (LoggingExcetion e) {
                // todo: log this exception to other loggers that are not failing
                e.printStackTrace();
            }
        });
    }
}
