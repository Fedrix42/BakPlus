package logging.notifications;


import java.awt.Component;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import logging.LoggerManager;

/**
 * Used mainly to notify to the user exception and errors that occurred.
 * Also useful for normal warning / info notifications.
 * @author Fedrix
 */
public class NotificationManager {
    
    /**
     * Notify the user via a dialog.
     * @param component the parent for the notification dialog
     * @param n the notification we want to display
     */
    public static void notify(Component component, SimpleNotification n) {
        if(n.level().intValue() <= Level.INFO.intValue()){
            JOptionPane.showMessageDialog(component, n.description(), "Hey, something happened!", JOptionPane.INFORMATION_MESSAGE, null);
        } else if(n.level().intValue() == Level.WARNING.intValue()){    
            JOptionPane.showMessageDialog(component, n.description(), "Warning!", JOptionPane.WARNING_MESSAGE, null);
        } else {
            JOptionPane.showMessageDialog(component, n.description(), "An error occurred!", JOptionPane.ERROR_MESSAGE, null);
        }
        
    }

    
    private static String messageBuilder(ExceptionNotification n){
        StringBuilder message = new StringBuilder();
        message.append(n.description());
        message.append(System.lineSeparator());
        message.append("- - - - -");
        message.append(System.lineSeparator());
        message.append(n.ex().toString());
        message.append(System.lineSeparator());
        message.append("--> StackTrace at %s".formatted(LoggerManager.logFile()));
        return message.toString();
    }
    
    /**
     * Notify the user via a dialog.
     * @param component the parent for the notification dialog
     * @param n the notification we want to display
     */
    public static void notify(Component component, ExceptionNotification n){
        String message = messageBuilder(n);
        if(n.level().intValue() == Level.WARNING.intValue()){    
            JOptionPane.showMessageDialog(component, message, "Warning!", JOptionPane.WARNING_MESSAGE, null);
        } else if(n.level().intValue() > Level.WARNING.intValue()){
            JOptionPane.showMessageDialog(component, message, "An error occurred!", JOptionPane.ERROR_MESSAGE, null);
        }
    }
    
}
