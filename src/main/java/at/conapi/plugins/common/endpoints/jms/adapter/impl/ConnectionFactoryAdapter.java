package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractConnection;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractConnectionFactory;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS ConnectionFactory.
 * <p>
 * This class wraps either a javax.jms.ConnectionFactory or jakarta.jms.ConnectionFactory
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
public class ConnectionFactoryAdapter implements AbstractConnectionFactory {
    private final Object connectionFactory;
    private final boolean isJakarta;

    /**
     * Constructs a ConnectionFactoryAdapter wrapping a vendor-specific connection factory.
     *
     * @param connectionFactory the underlying javax or jakarta JMS connection factory
     * @since 1.0.0
     */
    public ConnectionFactoryAdapter(Object connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.isJakarta = connectionFactory instanceof jakarta.jms.ConnectionFactory;
    }

    @Override
    public AbstractConnection createConnection() throws AbstractJMSException {
        try {
            Object connection = isJakarta 
                ? ((jakarta.jms.ConnectionFactory) connectionFactory).createConnection()
                : ((javax.jms.ConnectionFactory) connectionFactory).createConnection();
            return new ConnectionAdapter(connection);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create connection", e);
        }
    }

    @Override
    public AbstractConnection createConnection(String userName, String password) throws AbstractJMSException {
        try {
            Object connection = isJakarta
                    ? ((jakarta.jms.ConnectionFactory) connectionFactory).createConnection(userName, password)
                    : ((javax.jms.ConnectionFactory) connectionFactory).createConnection(userName, password);
            return new ConnectionAdapter(connection);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create connection", e);
        }
    }
}