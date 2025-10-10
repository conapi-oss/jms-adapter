package at.conapi.oss.jms.adapter.impl;

import at.conapi.oss.jms.adapter.AbstractConsumer;
import at.conapi.oss.jms.adapter.AbstractJMSException;
import at.conapi.oss.jms.adapter.AbstractMessage;
import at.conapi.oss.jms.adapter.AbstractMessageListener;

import java.util.concurrent.Callable;

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
    private final ClassLoader providerClassLoader;
    private AbstractMessageListener listener;

    /**
     * Constructs a ConsumerAdapter wrapping a vendor-specific message consumer.
     *
     * @param consumer the underlying javax or jakarta JMS message consumer
     * @param providerClassLoader the classloader that loaded the JMS provider classes
     * @since 1.0.0
     */
    public ConsumerAdapter(Object consumer, ClassLoader providerClassLoader) {
        this.consumer = consumer;
        this.isJakarta = consumer instanceof jakarta.jms.MessageConsumer;
        this.providerClassLoader = providerClassLoader;
    }

    @Override
    public void setMessageListener(AbstractMessageListener listener) throws AbstractJMSException {
        try {
            // Set provider classloader as thread context DURING setMessageListener() call
            // This ensures any worker threads created by the provider inherit the correct classloader
            ClassLoaderUtils.withContextClassLoader(providerClassLoader, () -> {
                if (isJakarta) {
                    ((jakarta.jms.MessageConsumer) consumer).setMessageListener(msg -> {
                        try {
                            listener.onMessage(new MessageAdapter(msg));
                        } catch (AbstractJMSException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    ((javax.jms.MessageConsumer) consumer).setMessageListener(msg -> {
                        try {
                            listener.onMessage(new MessageAdapter(msg));
                        } catch (AbstractJMSException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                return null;
            });
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to set message listener", e);
        }
    }

    @Override
    public AbstractMessage receive(long timeout) throws AbstractJMSException {
        try {
            // Set provider classloader as thread context during receive() operation
            // This is critical for providers like RabbitMQ that use Class.forName() during message deserialization
            return ClassLoaderUtils.withContextClassLoader(providerClassLoader, () -> {
                Object msg;
                if (isJakarta) {
                    msg = ((jakarta.jms.MessageConsumer) consumer).receive(timeout);
                } else {
                    msg = ((javax.jms.MessageConsumer) consumer).receive(timeout);
                }
                return msg != null ? new MessageAdapter(msg) : null;
            });
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
