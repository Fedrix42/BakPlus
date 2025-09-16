package core.tasks.exception;

/**
 * @author Fedrix
 */
public class InvalidEncryptionKeyException extends RuntimeException {

    /**
     * Creates a new instance of <code>InvalidEncryptionKeyException</code>
     * without detail message.
     */
    public InvalidEncryptionKeyException() {
    }

    /**
     * Constructs an instance of <code>InvalidEncryptionKeyException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidEncryptionKeyException(String msg) {
        super(msg);
    }
}
