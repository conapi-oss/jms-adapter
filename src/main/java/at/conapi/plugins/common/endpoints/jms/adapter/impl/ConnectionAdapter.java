package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.*;

import java.util.Objects;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS Connection.
 * <p>
 * This class wraps either a javax.jms.Connection or jakarta.jms.Connection
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
public class ConnectionAdapter implements AbstractConnection
{
    private final Object connection;
    private final boolean isJakarta;

    /**
     * Constructs a ConnectionAdapter wrapping a vendor-specific connection.
     *
     * @param connection the underlying javax or jakarta JMS connection
     * @since 1.0.0
     */
    public ConnectionAdapter(Object connection) {
        this.connection = connection;
        this.isJakarta = connection instanceof jakarta.jms.Connection;
    }

    @Override
    public AbstractSession createSession() throws AbstractJMSException {
        try {
            // for now use false, Session.AUTO_ACKNOWLEDGE
            Object session = isJakarta
                    ? ((jakarta.jms.Connection) connection).createSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE)
                        : ((javax.jms.Connection) connection).createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            return new SessionAdapter(session);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create session", e);
        }
    }

    @Override
    public void start() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.Connection) connection).start();
            } else {
                ((javax.jms.Connection) connection).start();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to start connection", e);
        }
    }

    @Override
    public void stop() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.Connection) connection).stop();
            } else {
                ((javax.jms.Connection) connection).stop();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to stop connection", e);
        }

    }

    @Override
    public void close() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.Connection) connection).close();
            } else {
                ((javax.jms.Connection) connection).close();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to close connection", e);
        }
    }

    @Override
    public void setExceptionListener(AbstractExceptionListener listener) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.Connection) connection).setExceptionListener( error -> {
                    try {
                        listener.onException(new AbstractJMSException("JMS Connection Error", error));
                    } catch (AbstractJMSException e) {
                        // TODO: handle exception
                        throw new RuntimeException(e);
                    }
                });
            } else {
                ((javax.jms.Connection) connection).setExceptionListener( error -> {
                    try {
                        listener.onException(new AbstractJMSException("JMS Connection Error", error));
                    } catch (AbstractJMSException e) {
                        // TODO: handle exception
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to set Exception Listener", e);
        }
    }
}