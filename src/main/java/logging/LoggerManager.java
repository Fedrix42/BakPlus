package logging;

import utils.formatters.FilenameDateFormatter;
import init.Environment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Manager which handles loggers
 * @author Fedrix
 */
public class LoggerManager {
    public static boolean isInit = false;
    private static final NotificationLogger exceptionNL = new NotificationLogger(Logger.getLogger("Exception Logger"));
    private static final NotificationLogger consoleNL = new NotificationLogger(Logger.getLogger("Console Logger"));
    private static Path logFile;
    
    /**
     * Inizialize logger environment such as log folder, log filename, ecc...
     * Multiple calls to this methods does nothing.
     * @throws IOException 
     */
    public static void init() throws IOException {
        if(!isInit){
            isInit = true;
            // Try init file handler for exception logger
            Path logFolder = Environment.getDataFolder().resolve("log");
            logFile = logFolder.resolve(FilenameDateFormatter.getFormattedNow() + ".log");
            if(!Files.exists(logFolder))
                Files.createDirectory(logFolder);
            FileHandler fh = new  FileHandler(logFile.toString(), true);
            fh.setFormatter(new SimpleFormatter());
            exceptionNL.logger.addHandler(fh);
            // Setting min level of logging
            exceptionNL.logger.setLevel(Level.WARNING);
            consoleNL.logger.setLevel(Level.INFO);
        }
    }
    
    /**
     * @return a logger which can be use to log exceptions and severe issues (above warning)
     */
    public static Logger exception(){
        if(!isInit) throw new IllegalStateException("Logger must be inizialed before use!");
        return exceptionNL.logger;
    }
    
    /**
     * @return a logger which can be use to log debug and info events
     */
    public static Logger console(){
        if(!isInit) throw new IllegalStateException("Logger must be inizialed before use!");
        return consoleNL.logger;
    }
    
    /**
     * @return a logger which can be use to log exceptions and severe issues (above warning)
     * by using notification record objects
     */
    public static NotificationLogger exceptionNL(){
        if(!isInit) throw new IllegalStateException("Logger must be inizialed before use!");
        return exceptionNL;
    }
    
    /**
     * @return a logger which can be use to log debug and info events
     * by using notification record objects
     */
    public static NotificationLogger consoleNL(){
        if(!isInit) throw new IllegalStateException("Logger must be inizialed before use!");
        return consoleNL;
    }

    public static String logFile(){
        if(!isInit) throw new IllegalStateException("Logger must be inizialed before use!");
        return logFile.toString();
    }
    
}
