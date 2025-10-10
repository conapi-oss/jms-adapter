package at.conapi.oss.jms.adapter;

import java.util.Map;

/**
 * Vendor-neutral interface for JMS session operations.
 * <p>
 * A Session is a single-threaded context for producing and consuming messages. Sessions are used
 * to create message producers, consumers, messages, and destinations.
 * </p>
 * <p>
 * This interface extends {@link AutoCloseable} to support try-with-resources statements.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractSession extends AutoCloseable {

    /**
     * Creates a message producer for the specified destination.
     *
     * @param destination the destination (Queue or Topic) to send messages to
     * @return a new message producer
     * @throws AbstractJMSException if producer creation fails
     * @since 1.0.0
     */
    AbstractProducer createProducer(AbstractDestination destination) throws AbstractJMSException;

    /**
     * Creates a message consumer for the specified destination.
     *
     * @param destination the destination (Queue or Topic) to receive messages from
     * @return a new message consumer
     * @throws AbstractJMSException if consumer creation fails
     * @since 1.0.0
     */
    AbstractConsumer createConsumer(AbstractDestination destination) throws AbstractJMSException;

    /**
     * Creates a message consumer with a message selector filter.
     * <p>
     * Only messages matching the selector will be delivered to this consumer.
     * </p>
     *
     * @param destination the destination (Queue or Topic) to receive messages from
     * @param msgSelector JMS message selector expression (SQL-92 syntax)
     * @return a new message consumer with selector
     * @throws AbstractJMSException if consumer creation fails or selector is invalid
     * @since 1.0.0
     */
    AbstractConsumer createConsumer(AbstractDestination destination, String msgSelector) throws AbstractJMSException;

    /**
     * Creates a destination (Queue or Topic) by name.
     * <p>
     * The destination type is determined by the provider's configuration.
     * </p>
     *
     * @param destination the destination name
     * @return a new destination reference
     * @throws AbstractJMSException if destination creation fails
     * @since 1.0.0
     */
    AbstractDestination createDestination(String destination) throws AbstractJMSException;

    /**
     * Creates a temporary queue for request-reply messaging patterns.
     * <p>
     * Temporary queues are automatically deleted when the connection is closed.
     * They are typically used as reply-to destinations.
     * </p>
     *
     * @return a new temporary queue
     * @throws AbstractJMSException if temporary queue creation fails
     * @since 1.0.0
     */
    AbstractDestination createTemporaryQueue() throws AbstractJMSException;

    /**
     * Creates a generic message with optional properties.
     *
     * @param body the message body (text content)
     * @param properties message properties to set (may be null or empty)
     * @return a new message
     * @throws AbstractJMSException if message creation fails
     * @since 1.0.0
     */
    AbstractMessage createMessage(String body, Map<String, Object> properties) throws AbstractJMSException;

    /**
     * Creates a text message with optional properties.
     *
     * @param body the text message body
     * @param properties message properties to set (may be null or empty)
     * @return a new text message
     * @throws AbstractJMSException if message creation fails
     * @since 1.0.0
     */
    AbstractMessage createTextMessage(String body, Map<String, Object> properties) throws AbstractJMSException;

    /**
     * Creates a bytes message with optional properties.
     *
     * @param body the message body as byte array
     * @param properties message properties to set (may be null or empty)
     * @return a new bytes message
     * @throws AbstractJMSException if message creation fails
     * @since 1.0.0
     */
    AbstractMessage createBytesMessage(byte[] body, Map<String, Object> properties) throws AbstractJMSException;

    /**
     * Closes the session and releases all associated resources.
     * <p>
     * This method closes all producers and consumers created from this session.
     * Once closed, the session cannot be reused.
     * </p>
     *
     * @throws AbstractJMSException if an error occurs during close
     * @since 1.0.0
     */
    void close() throws AbstractJMSException;
}