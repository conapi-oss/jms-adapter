package at.conapi.oss.jms.adapter;

/**
 * Listener interface for asynchronous JMS message delivery.
 * <p>
 * Implementations of this interface receive messages asynchronously when registered
 * with a MessageConsumer via {@link AbstractConsumer#setMessageListener(AbstractMessageListener)}.
 * </p>
 * <p>
 * The JMS provider calls {@link #onMessage(AbstractMessage)} when a message arrives
 * for the consumer. The connection must be started for message delivery.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractMessageListener {

    /**
     * Called when a message arrives for asynchronous delivery.
     * <p>
     * This method is called by the JMS provider on a separate thread. Implementations
     * should process messages quickly to avoid blocking message delivery to other consumers.
     * </p>
     *
     * @param message the delivered message
     * @throws AbstractJMSException if message processing fails
     * @since 1.0.0
     */
    void onMessage(AbstractMessage message) throws AbstractJMSException;
}
