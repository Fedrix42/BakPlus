package logging;

import java.util.logging.Logger;
import logging.notifications.ExceptionNotification;
import logging.notifications.SimpleNotification;

/**
 * A logger decorator compatible with notification system which can use ExceptionNotification
 * and SimpleNotification objects to log events
 * @author fedrix
 */
public class NotificationLogger {
    final Logger logger;
    
    protected NotificationLogger(Logger logger) {
        this.logger = logger;
    }
    
    
    public void log(ExceptionNotification notification){
        logger.log(notification.level(), notification.description(), notification.ex());
    }
    
    public void log(SimpleNotification notification){
        logger.log(notification.level(), notification.description());
    }
    
    
    public Logger logger(){
        return logger;
    }
    
    
    
}
