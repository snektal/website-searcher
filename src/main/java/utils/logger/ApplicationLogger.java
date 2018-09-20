package utils.logger;

import utils.config.ApplicationConfig;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ApplicationLogger {

    private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",  "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }
    private ApplicationLogger() {
    }

    public static void setup(String timestamp) throws IOException {
        // get the global logger to configure it
        logger.setLevel(getLevelFromConfig());
        createLogsDirectory();

        String filePattern = getFileName(timestamp);
        System.out.println("Outputting logs to file: " + filePattern);

        FileHandler fileHandler = new FileHandler(filePattern);

        // create a TXT formatter
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileHandler.setFormatter(formatterTxt);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);

    }

    public static void log(Level level, String message, Object... arguments) {
        if (logger.isLoggable(level))
            logger.log(level, MessageFormat.format(message, arguments));
    }

    private static void createLogsDirectory() {
        File logDir = new File("./logs/");
        if (!(logDir.exists())) {
            logDir.mkdir();
        }
    }

    private static String getFileName(String timestamp) {
        return "logs/websearcher_log_" + timestamp + ".txt";
    }

    private static Level getLevelFromConfig() {
        String levelConfig = ApplicationConfig.getStringProperty("logger.level", "INFO");
        return Level.parse(levelConfig);
    }
}
