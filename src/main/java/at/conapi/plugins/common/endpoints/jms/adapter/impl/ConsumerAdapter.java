package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractConsumer;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractMessage;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractMessageListener;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS MessageConsumer.
 * <p>
 * This class wraps either a javax.jms.MessageConsumer or jakarta.jms.MessageConsumer
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
public class ConsumerAdapter implements AbstractConsumer {
    private final Object consumer;
    private final boolean isJakarta;
    private AbstractMessageListener listener;

    /**
     * Constructs a ConsumerAdapter wrapping a vendor-specific message consumer.
     *
     * @param consumer the underlying javax or jakarta JMS message consumer
     * @since 1.0.0
     */
    public ConsumerAdapter(Object consumer) {
        this.consumer = consumer;
        this.isJakarta = consumer instanceof jakarta.jms.MessageConsumer;
    }

    @Override
    public void setMessageListener(AbstractMessageListener listener) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageConsumer) consumer).setMessageListener(msg -> {
                    try {
                        listener.onMessage(new MessageAdapter(msg));
                    } catch (AbstractJMSException e) {
                        // TODO: handle exception
                        throw new RuntimeException(e);
                    }
                });
            } else {
                ((javax.jms.MessageConsumer) consumer).setMessageListener(msg -> {
                    try {
                        listener.onMessage(new MessageAdapter(msg));
                    } catch (AbstractJMSException e) {
                        // TODO: handle exception
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to set message listener", e);
        }
    }

    @Override
    public AbstractMessage receive(long timeout) throws AbstractJMSException {
        try {
            Object msg;
            if (isJakarta) {
                msg = ((jakarta.jms.MessageConsumer) consumer).receive(timeout);
            } else {
                msg = ((javax.jms.MessageConsumer) consumer).receive(timeout);
            }
            return msg!=null ? new MessageAdapter(msg) : null;
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to receive message", e);
        }
    }


    @Override
    public void close() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageConsumer) consumer).close();
            } else {
                ((javax.jms.MessageConsumer) consumer).close();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to close consumer", e);
        }
    }
}
