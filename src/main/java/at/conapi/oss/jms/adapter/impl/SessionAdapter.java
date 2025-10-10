package at.conapi.oss.jms.adapter.impl;

import at.conapi.oss.jms.adapter.*;
import at.conapi.oss.jms.adapter.*;
import java.util.Map;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS Session.
 * <p>
 * This class wraps either a javax.jms.Session or jakarta.jms.Session
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
public class SessionAdapter implements AbstractSession
{
    private final Object session;
    private final boolean isJakarta;
    private final ClassLoader providerClassLoader;

    /** Destination type constant for queue destinations. */
    public static final String QUEUE = "queue";
    /** Destination type constant for topic destinations. */
    public static final String TOPIC = "topic";
    /** Destination type constant for JNDI destinations. */
    public static final String JNDI = "jndi";

    /**
     * Constructs a SessionAdapter wrapping a vendor-specific session.
     *
     * @param session the underlying javax or jakarta JMS session
     * @param providerClassLoader the classloader that loaded the JMS provider classes
     * @since 1.0.0
     */
    public SessionAdapter(Object session, ClassLoader providerClassLoader) {
        this.session = session;
        this.isJakarta = session instanceof jakarta.jms.Session;
        this.providerClassLoader = providerClassLoader;
    }

    @Override
    public AbstractProducer createProducer(AbstractDestination destination) throws AbstractJMSException
    {
        try {
            Object producer = isJakarta
                    ? ((jakarta.jms.Session) session).createProducer((jakarta.jms.Destination) destination.getDestination())
                        : ((javax.jms.Session) session).createProducer((javax.jms.Destination) destination.getDestination());
            return new ProducerAdapter(producer);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create session", e);
        }
    }

    @Override
    public AbstractConsumer createConsumer(AbstractDestination destination) throws AbstractJMSException {
        try {
            Object consumer = isJakarta
                    ? ((jakarta.jms.Session) session).createConsumer((jakarta.jms.Destination) destination.getDestination())
                        : ((javax.jms.Session) session).createConsumer((javax.jms.Destination) destination.getDestination());
            return new ConsumerAdapter(consumer, providerClassLoader);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create session", e);
        }
    }


    @Override
    public AbstractConsumer createConsumer(AbstractDestination destination, String msgSelector) throws AbstractJMSException {
        try {
            Object consumer = isJakarta
                    ? ((jakarta.jms.Session) session).createConsumer((jakarta.jms.Destination) destination.getDestination(), msgSelector)
                    : ((javax.jms.Session) session).createConsumer((javax.jms.Destination) destination.getDestination(), msgSelector);
            return new ConsumerAdapter(consumer, providerClassLoader);
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create session", e);
        }
    }


    @Override
    public AbstractDestination createDestination(String destinationUrl) throws AbstractJMSException {
        // they are of the form "queue://test" or "topic://test"
        String destinationName = destinationUrl.substring(destinationUrl.indexOf("://") + 3);
        String destinationType = destinationUrl.substring(0, destinationUrl.indexOf("://"));

        switch (destinationType) {
            case QUEUE:
                return createQueue(destinationName);
            case TOPIC:
                return createTopic(destinationName);
           // case JNDI:
            //    return createJndiDestination(destinationName);
            default:
                throw new IllegalArgumentException("Invalid destination type: " + destinationType);
        }
    }

    @Override
    public AbstractDestination createTemporaryQueue() throws AbstractJMSException {
        try {
            if (isJakarta) {
                return new DestinationAdapter(((jakarta.jms.Session) session).createTemporaryQueue());
            } else {
                return new DestinationAdapter(((javax.jms.Session) session).createTemporaryQueue());
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create temporary queue", e);
        }
    }

    @Override
    public AbstractMessage createTextMessage(String body, Map<String, Object> properties) throws AbstractJMSException {
        try {
            if (isJakarta) {
                jakarta.jms.TextMessage jmsMessage = ((jakarta.jms.Session) session).createTextMessage(body);
                setProperties(jmsMessage, properties);
                return new MessageAdapter(jmsMessage);
            } else {
                javax.jms.TextMessage jmsMessage = ((javax.jms.Session) session).createTextMessage(body);
                setProperties(jmsMessage, properties);
                return new MessageAdapter(jmsMessage);
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create TextMessage", e);
        }
    }

    @Override
    public AbstractMessage createBytesMessage(byte[] body, Map<String, Object> properties) throws AbstractJMSException {
        try {
            if (isJakarta) {
                jakarta.jms.BytesMessage jmsMessage = ((jakarta.jms.Session) session).createBytesMessage();
                if (body != null) {
                    jmsMessage.writeBytes(body);
                }
                setProperties(jmsMessage, properties);
                return new MessageAdapter(jmsMessage);
            } else {
                javax.jms.BytesMessage jmsMessage = ((javax.jms.Session) session).createBytesMessage();
                if (body != null) {
                    jmsMessage.writeBytes(body);
                }
                setProperties(jmsMessage, properties);
                return new MessageAdapter(jmsMessage);
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create BytesMessage", e);
        }
    }

    /**
     * Helper method to set properties on any JMS message type.
     */
    private void setProperties(Object jmsMessage, Map<String, Object> properties) throws Exception {
        if (properties == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (isJakarta) {
                ((jakarta.jms.Message) jmsMessage).setObjectProperty(entry.getKey(), entry.getValue());
            } else {
                ((javax.jms.Message) jmsMessage).setObjectProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public AbstractMessage createMessage(String body, Map<String, Object> properties) throws AbstractJMSException {
        try {
            if (isJakarta) {
                jakarta.jms.Message jmsMessage = ((jakarta.jms.Session) session).createTextMessage(body);
                if (properties != null) {
                    properties.forEach((key, value) -> {
                        try {
                            jmsMessage.setObjectProperty(key, value);
                        } catch (jakarta.jms.JMSException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                return new MessageAdapter(jmsMessage);
            } else {
                javax.jms.Message jmsMessage = ((javax.jms.Session) session).createTextMessage(body);
                if (properties != null) {
                    properties.forEach((key, value) -> {
                        try {
                            jmsMessage.setObjectProperty(key, value);
                        } catch (javax.jms.JMSException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                return new MessageAdapter(jmsMessage);
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to close session", e);
        }
    }


    @Override
    public void close() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.Session) session).close();
            } else {
                ((javax.jms.Session) session).close();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to close session", e);
        }
    }

    private AbstractDestination createQueue(String destinationName) throws AbstractJMSException {
        try {
            return new DestinationAdapter(isJakarta
                    ? ((jakarta.jms.Session) session).createQueue(destinationName)
                        : ((javax.jms.Session) session).createQueue(destinationName));
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create queue", e);
        }
    }

    private AbstractDestination createTopic(String destinationName) throws AbstractJMSException {
        try {
            return new DestinationAdapter(isJakarta
                    ? ((jakarta.jms.Session) session).createTopic(destinationName)
                        : ((javax.jms.Session) session).createTopic(destinationName));
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to create topic", e);
        }
    }

}