package at.conapi.oss.jms.adapter;

/**
 * Vendor-neutral exception for JMS operations.
 * <p>
 * This exception wraps vendor-specific JMS exceptions, providing a unified
 * error handling interface across different JMS providers and namespaces
 * (javax.jms and jakarta.jms).
 * </p>
 *
 * @since 1.0.0
 */
public class AbstractJMSException extends Exception {

    /**
     * Constructs an exception with a message and underlying cause.
     *
     * @param message the error message
     * @param cause the underlying exception
     * @since 1.0.0
     */
    public AbstractJMSException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an exception with only a message.
     *
     * @param message the error message
     * @since 1.0.0
     */
    public AbstractJMSException(String message) {
        super(message);
    }
}