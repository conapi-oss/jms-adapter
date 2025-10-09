package at.conapi.plugins.common.endpoints.jms.adapter;

/**
 * Vendor-neutral interface for receiving JMS messages.
 * <p>
 * A MessageConsumer is created from a Session and is used to receive messages from a destination.
 * Messages can be received synchronously via {@link #receive(long)} or asynchronously via
 * {@link #setMessageListener(AbstractMessageListener)}.
 * </p>
 * <p>
 * This interface extends {@link AutoCloseable} to support try-with-resources statements.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractConsumer extends AutoCloseable {

    /**
     * Sets a message listener for asynchronous message delivery.
     * <p>
     * When a listener is set, messages are delivered asynchronously as they arrive.
     * The connection must be started for message delivery to begin.
     * </p>
     *
     * @param listener the message listener, or null to remove the current listener
     * @throws AbstractJMSException if the listener cannot be set
     * @since 1.0.0
     */
    void setMessageListener(AbstractMessageListener listener) throws AbstractJMSException;

    /**
     * Receives the next message synchronously within the specified timeout.
     * <p>
     * This method blocks until a message arrives, the timeout expires, or the consumer is closed.
     * The connection must be started for message delivery.
     * </p>
     *
     * @param timeout the timeout in milliseconds, or 0 to wait indefinitely
     * @return the next message, or null if the timeout expires
     * @throws AbstractJMSException if message reception fails
     * @since 1.0.0
     */
    AbstractMessage receive(long timeout) throws AbstractJMSException;

    /**
     * Closes the consumer and releases all associated resources.
     *
     * @throws AbstractJMSException if an error occurs during close
     * @since 1.0.0
     */
    void close() throws AbstractJMSException;
}
