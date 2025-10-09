package at.conapi.plugins.common.endpoints.jms.adapter;

/**
 * Vendor-neutral interface for sending JMS messages.
 * <p>
 * A MessageProducer is created from a Session and is used to send messages to a destination.
 * </p>
 * <p>
 * This interface extends {@link AutoCloseable} to support try-with-resources statements.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractProducer extends AutoCloseable{

    /**
     * Sends a message using default delivery mode, priority, and time-to-live.
     *
     * @param message the message to send
     * @throws AbstractJMSException if the message cannot be sent
     * @since 1.0.0
     */
    void send(AbstractMessage message) throws AbstractJMSException;

    /**
     * Sends a message with specified delivery mode.
     *
     * @param message the message to send
     * @param deliveryMode the delivery mode (persistent or non-persistent)
     * @throws AbstractJMSException if the message cannot be sent
     * @since 1.0.0
     */
    void send(AbstractMessage message, int deliveryMode) throws AbstractJMSException;

    /**
     * Sends a message with specified delivery mode and priority.
     *
     * @param message the message to send
     * @param deliveryMode the delivery mode (persistent or non-persistent)
     * @param priority message priority (0-9, where 0 is lowest and 9 is highest)
     * @throws AbstractJMSException if the message cannot be sent
     * @since 1.0.0
     */
    void send(AbstractMessage message, int deliveryMode, int priority) throws AbstractJMSException;

    /**
     * Sends a message with full control over delivery parameters.
     *
     * @param message the message to send
     * @param deliveryMode the delivery mode (persistent or non-persistent)
     * @param priority message priority (0-9, where 0 is lowest and 9 is highest)
     * @param timeToLive message lifetime in milliseconds, or 0 for unlimited
     * @throws AbstractJMSException if the message cannot be sent
     * @since 1.0.0
     */
    void send(AbstractMessage message, int deliveryMode, int priority, long timeToLive) throws AbstractJMSException;

    /**
     * Closes the producer and releases all associated resources.
     *
     * @throws AbstractJMSException if an error occurs during close
     * @since 1.0.0
     */
    void close() throws AbstractJMSException;
}
