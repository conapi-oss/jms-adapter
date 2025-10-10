package at.conapi.oss.jms.adapter.impl;

import at.conapi.oss.jms.adapter.AbstractConnection;
import at.conapi.oss.jms.adapter.AbstractConnectionFactory;
import at.conapi.oss.jms.adapter.AbstractJMSException;

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
    private final ClassLoader providerClassLoader;

    /**
     * Constructs a ConnectionFactoryAdapter wrapping a vendor-specific connection factory.
     *
     * @param connectionFactory the underlying javax or jakarta JMS connection factory
     * @param providerClassLoader the classloader that loaded the JMS provider classes
     * @since 1.0.0
     */
    public ConnectionFactoryAdapter(Object connectionFactory, ClassLoader providerClassLoader) {
        this.connectionFactory = connectionFactory;
        this.isJakarta = connectionFactory instanceof jakarta.jms.ConnectionFactory;
        this.providerClassLoader = providerClassLoader;
    }

    @Override
    public AbstractConnection createConnection() throws AbstractJMSException {
        try {
            // Set provider classloader as thread context DURING connection creation
            // This is critical because some providers (e.g., RabbitMQ) create worker threads
            // during connection construction, and those threads inherit the context classloader
            return ClassLoaderUtils.withContextClassLoader(providerClassLoader, () -> {
                Object connection = isJakarta
                    ? ((jakarta.jms.ConnectionFactory) connectionFactory).createConnection()
                    : ((javax.jms.ConnectionFactory) connectionFactory).createConnection();
                return new ConnectionAdapter(connection, providerClassLoader);
            });
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create connection", e);
        }
    }

    @Override
    public AbstractConnection createConnection(String userName, String password) throws AbstractJMSException {
        try {
            // Set provider classloader as thread context DURING connection creation
            // This is critical because some providers (e.g., RabbitMQ) create worker threads
            // during connection construction, and those threads inherit the context classloader
            return ClassLoaderUtils.withContextClassLoader(providerClassLoader, () -> {
                Object connection = isJakarta
                    ? ((jakarta.jms.ConnectionFactory) connectionFactory).createConnection(userName, password)
                    : ((javax.jms.ConnectionFactory) connectionFactory).createConnection(userName, password);
                return new ConnectionAdapter(connection, providerClassLoader);
            });
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create connection", e);
        }
    }
}