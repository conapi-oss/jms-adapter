package at.conapi.oss.jms.adapter;

/**
 * Vendor-neutral interface for creating JMS connections.
 * <p>
 * This interface provides a unified abstraction over vendor-specific ConnectionFactory
 * implementations, supporting both javax.jms and jakarta.jms namespaces.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractConnectionFactory {

    /**
     * Creates a JMS connection with default credentials.
     * <p>
     * The connection is not started automatically. Call {@link AbstractConnection#start()}
     * before receiving messages.
     * </p>
     *
     * @return a new JMS connection
     * @throws AbstractJMSException if connection creation fails
     * @since 1.0.0
     */
    AbstractConnection createConnection() throws AbstractJMSException;

    /**
     * Creates a JMS connection with specified credentials.
     * <p>
     * The connection is not started automatically. Call {@link AbstractConnection#start()}
     * before receiving messages.
     * </p>
     *
     * @param userName the username for authentication
     * @param password the password for authentication
     * @return a new JMS connection
     * @throws AbstractJMSException if connection creation or authentication fails
     * @since 1.0.0
     */
    AbstractConnection createConnection(String userName, String password) throws AbstractJMSException;
}



