package at.conapi.plugins.common.endpoints.jms.adapter;

/**
 * Vendor-neutral interface for managing JMS connections.
 * <p>
 * A Connection manages client connectivity to a JMS provider and is used to create sessions.
 * Connections must be started before message consumption can begin.
 * </p>
 * <p>
 * This interface extends {@link AutoCloseable} to support try-with-resources statements.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractConnection extends AutoCloseable {

    /**
     * Creates a new JMS session.
     * <p>
     * Multiple sessions can be created from a single connection. Each session provides
     * an independent transactional context.
     * </p>
     *
     * @return a new JMS session
     * @throws AbstractJMSException if session creation fails
     * @since 1.0.0
     */
    AbstractSession createSession() throws AbstractJMSException;

    /**
     * Starts (or restarts) message delivery for this connection.
     * <p>
     * A connection must be started before any consumers can receive messages.
     * Calling start on an already-started connection has no effect.
     * </p>
     *
     * @throws AbstractJMSException if the connection cannot be started
     * @since 1.0.0
     */
    void start() throws AbstractJMSException;

    /**
     * Temporarily stops message delivery for this connection.
     * <p>
     * Message delivery can be resumed by calling {@link #start()}.
     * </p>
     *
     * @throws AbstractJMSException if the connection cannot be stopped
     * @since 1.0.0
     */
    void stop() throws AbstractJMSException;

    /**
     * Closes the connection and releases all associated resources.
     * <p>
     * This method closes all sessions, producers, and consumers created from this connection.
     * Once closed, the connection cannot be reused.
     * </p>
     *
     * @throws AbstractJMSException if an error occurs during close
     * @since 1.0.0
     */
    void close() throws AbstractJMSException;

    /**
     * Sets an exception listener to be notified of connection-level errors.
     * <p>
     * The exception listener is called asynchronously when the connection encounters
     * a serious error that is not associated with a specific operation.
     * </p>
     *
     * @param listener the exception listener to set, or null to remove the current listener
     * @throws AbstractJMSException if the listener cannot be set
     * @since 1.0.0
     */
    void setExceptionListener(AbstractExceptionListener listener) throws AbstractJMSException;
}
