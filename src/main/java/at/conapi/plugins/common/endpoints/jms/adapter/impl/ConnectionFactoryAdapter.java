package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractConnection;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractConnectionFactory;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;


public class ConnectionFactoryAdapter implements AbstractConnectionFactory {
    private final Object connectionFactory;
    private final boolean isJakarta;

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