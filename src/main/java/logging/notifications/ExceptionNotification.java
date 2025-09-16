package logging.notifications;

import static java.util.Objects.requireNonNull;
import java.util.logging.Level;

/**
 *
 * @author Fedrix
 */
public record ExceptionNotification(String description, Throwable ex, Level level) {
    public ExceptionNotification(String description, Throwable ex, Level level) {
        this.description = requireNonNull(description);
        this.ex = requireNonNull(ex);
        this.level = requireNonNull(level);
    }
    
}
