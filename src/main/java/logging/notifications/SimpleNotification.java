package logging.notifications;

import static java.util.Objects.requireNonNull;
import java.util.logging.Level;


/**
 * Represent a notification using sort of decorator pattern
 */
public record SimpleNotification(String description, Level level) {
    
    public SimpleNotification(String description, Level level) {
        this.description = requireNonNull(description);
        this.level = requireNonNull(level);
    }
    
    /**
     * Create a simple notification with the default notification level (informational message)
     * @param description 
     */
    public SimpleNotification(String description){
        this(description, Level.INFO);
    }
}
