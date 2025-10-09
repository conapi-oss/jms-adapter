package at.conapi.plugins.common.endpoints.jms.adapter;

/**
 * Listener interface for asynchronous notification of JMS connection errors.
 * <p>
 * Implementations of this interface receive notifications when a JMS connection
 * encounters a serious error that is not associated with a specific operation.
 * </p>
 * <p>
 * Register an exception listener with {@link AbstractConnection#setExceptionListener(AbstractExceptionListener)}
 * to be notified of connection-level errors.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractExceptionListener {

    /**
     * Called when the JMS connection encounters a serious error.
     * <p>
     * This method is called asynchronously by the JMS provider when a connection-level
     * error occurs (e.g., network failure, broker shutdown).
     * </p>
     *
     * @param exception the exception describing the error
     * @throws AbstractJMSException if error handling fails
     * @since 1.0.0
     */
    public void onException(AbstractJMSException exception) throws AbstractJMSException;
}
