package core.strategy.exception;

/**
 * Thrown when an attempt was made to create a new strategy
 * but there is already a one with the same name
 * @author Fedrix
 */
public class StrategyAlreadyExistsException extends Exception {

    /**
     * Creates a new instance of <code>StrategyAlreadyExistsException</code>
     * with default message
     */
    public StrategyAlreadyExistsException() {
        super("Strategy already exists. Check input name.");
    }

    /**
     * Constructs an instance of <code>StrategyAlreadyExistsException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public StrategyAlreadyExistsException(String msg) {
        super(msg);
    }
}
