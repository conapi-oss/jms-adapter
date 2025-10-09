package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractDestination;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS Message.
 * <p>
 * This class wraps either a javax.jms.Message or jakarta.jms.Message
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
public class MessageAdapter implements AbstractMessage {

    private final Object message;
    private final boolean isJakarta;

    @FunctionalInterface
    private interface JmsFunction<T, R> {
        R apply(T t) throws jakarta.jms.JMSException, javax.jms.JMSException;
    }

    @FunctionalInterface
    private interface JmsConsumer<T> {
        void accept(T t) throws jakarta.jms.JMSException, javax.jms.JMSException;
    }

    /**
     * Constructs a MessageAdapter wrapping a vendor-specific message.
     *
     * @param message the underlying javax or jakarta JMS message
     * @throws IllegalArgumentException if message is not a valid JMS message instance
     * @since 1.0.0
     */
    public MessageAdapter(Object message) {
        if (!(message instanceof jakarta.jms.Message) && !(message instanceof javax.jms.Message)) {
            throw new IllegalArgumentException("Object must be an instance of a JMS Message");
        }
        this.message = message;
        this.isJakarta = message instanceof jakarta.jms.Message;
    }

    // --- Private Helper Methods to Centralize Logic ---

    /**
     * Executes a function on the underlying JMS message, handling the namespace difference.
     * @param <R> The return type.
     * @param action The function to apply to the message object.
     * @return The result of the function.
     */
    private <R> R execute(JmsFunction<Object, R> action) throws AbstractJMSException {
        try {
            return action.apply(message);
        } catch (jakarta.jms.JMSException | javax.jms.JMSException e) {
            // Catch specific JMS exceptions
            String methodName = new Throwable().getStackTrace()[1].getMethodName();
            throw new AbstractJMSException("JMS error in " + methodName, e);
        } catch (Exception e) {
            // Catch any other unexpected errors
            String methodName = new Throwable().getStackTrace()[1].getMethodName();
            throw new AbstractJMSException("Failed to execute " + methodName, e);
        }
    }

    /**
     * Executes a consumer on the underlying JMS message, handling the namespace difference.
     * @param action The consumer action to apply to the message object.
     */
    private void executeConsumer(JmsConsumer<Object> action) throws AbstractJMSException {
        try {
            action.accept(message);
        } catch (jakarta.jms.JMSException | javax.jms.JMSException e) {
            String methodName = new Throwable().getStackTrace()[1].getMethodName();
            throw new AbstractJMSException("JMS error in " + methodName, e);
        } catch (Exception e) {
            String methodName = new Throwable().getStackTrace()[1].getMethodName();
            throw new AbstractJMSException("Failed to execute " + methodName, e);
        }
    }

    // --- Standard JMS Message Getters/Setters (Complete Implementation) ---

    @Override
    public String getJMSMessageID() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSMessageID() : ((javax.jms.Message) msg).getJMSMessageID());
    }

    @Override
    public void setJMSMessageID(String id) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSMessageID(id);
            else ((javax.jms.Message) msg).setJMSMessageID(id);
        });
    }

    @Override
    public long getJMSTimestamp() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSTimestamp() : ((javax.jms.Message) msg).getJMSTimestamp());
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSTimestamp(timestamp);
            else ((javax.jms.Message) msg).setJMSTimestamp(timestamp);
        });
    }

    @Override
    public String getJMSCorrelationID() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSCorrelationID() : ((javax.jms.Message) msg).getJMSCorrelationID());
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSCorrelationID(correlationID);
            else ((javax.jms.Message) msg).setJMSCorrelationID(correlationID);
        });
    }

    @Override
    public AbstractDestination getJMSReplyTo() throws AbstractJMSException {
        return execute(msg -> {
            Object dest = isJakarta ? ((jakarta.jms.Message) msg).getJMSReplyTo() : ((javax.jms.Message) msg).getJMSReplyTo();
            return dest != null ? new DestinationAdapter(dest) : null;
        });
    }

    @Override
    public void setJMSReplyTo(AbstractDestination replyTo) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSReplyTo((jakarta.jms.Destination) replyTo.getDestination());
            else ((javax.jms.Message) msg).setJMSReplyTo((javax.jms.Destination) replyTo.getDestination());
        });
    }

    @Override
    public AbstractDestination getJMSDestination() throws AbstractJMSException {
        return execute(msg -> {
            Object dest = isJakarta ? ((jakarta.jms.Message) msg).getJMSDestination() : ((javax.jms.Message) msg).getJMSDestination();
            return dest != null ? new DestinationAdapter(dest) : null;
        });
    }

    @Override
    public void setJMSDestination(AbstractDestination destination) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSDestination((jakarta.jms.Destination) destination.getDestination());
            else ((javax.jms.Message) msg).setJMSDestination((javax.jms.Destination) destination.getDestination());
        });
    }

    @Override
    public int getJMSDeliveryMode() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSDeliveryMode() : ((javax.jms.Message) msg).getJMSDeliveryMode());
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSDeliveryMode(deliveryMode);
            else ((javax.jms.Message) msg).setJMSDeliveryMode(deliveryMode);
        });
    }

    @Override
    public boolean getJMSRedelivered() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSRedelivered() : ((javax.jms.Message) msg).getJMSRedelivered());
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSRedelivered(redelivered);
            else ((javax.jms.Message) msg).setJMSRedelivered(redelivered);
        });
    }

    @Override
    public String getJMSType() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSType() : ((javax.jms.Message) msg).getJMSType());
    }

    @Override
    public void setJMSType(String type) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSType(type);
            else ((javax.jms.Message) msg).setJMSType(type);
        });
    }

    @Override
    public long getJMSExpiration() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSExpiration() : ((javax.jms.Message) msg).getJMSExpiration());
    }

    @Override
    public void setJMSExpiration(long expiration) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSExpiration(expiration);
            else ((javax.jms.Message) msg).setJMSExpiration(expiration);
        });
    }

    @Override
    public long getJMSDeliveryTime() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSDeliveryTime() : ((javax.jms.Message) msg).getJMSDeliveryTime());
    }

    @Override
    public void setJMSDeliveryTime(long deliveryTime) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSDeliveryTime(deliveryTime);
            else ((javax.jms.Message) msg).setJMSDeliveryTime(deliveryTime);
        });
    }

    @Override
    public int getJMSPriority() throws AbstractJMSException {
        return execute(msg -> isJakarta ? ((jakarta.jms.Message) msg).getJMSPriority() : ((javax.jms.Message) msg).getJMSPriority());
    }

    @Override
    public void setJMSPriority(int priority) throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).setJMSPriority(priority);
            else ((javax.jms.Message) msg).setJMSPriority(priority);
        });
    }

    @Override
    public void acknowledge() throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).acknowledge();
            else ((javax.jms.Message) msg).acknowledge();
        });
    }

    @Override
    public void clearBody() throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).clearBody();
            else ((javax.jms.Message) msg).clearBody();
        });
    }

    @Override
    public void clearProperties() throws AbstractJMSException {
        executeConsumer(msg -> {
            if (isJakarta) ((jakarta.jms.Message) msg).clearProperties();
            else ((javax.jms.Message) msg).clearProperties();
        });
    }

    @Override
    public Map<String, Object> getProperties() throws AbstractJMSException {
        return execute(msg -> {
            Map<String, Object> properties = new HashMap<>();
            Enumeration<?> names = isJakarta ? ((jakarta.jms.Message) msg).getPropertyNames() : ((javax.jms.Message) msg).getPropertyNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                Object value = isJakarta ? ((jakarta.jms.Message) msg).getObjectProperty(name) : ((javax.jms.Message) msg).getObjectProperty(name);
                properties.put(name, value);
            }
            return properties;
        });
    }

    @Override
    public String getText() throws AbstractJMSException {
        return execute(msg -> isJakarta
                ? ((jakarta.jms.TextMessage) msg).getText()
                : ((javax.jms.TextMessage) msg).getText()
        );
    }

    @Override
    public byte[] getByteArray() throws AbstractJMSException {
        return execute(msg -> {
            if (isJakarta) {
                jakarta.jms.BytesMessage bytesMessage = (jakarta.jms.BytesMessage) msg;
                int bodyLength = Math.toIntExact(bytesMessage.getBodyLength());
                byte[] messageBytes = new byte[bodyLength];
                bytesMessage.readBytes(messageBytes);
                return messageBytes;
            } else {
                javax.jms.BytesMessage bytesMessage = (javax.jms.BytesMessage) msg;
                int bodyLength = Math.toIntExact(bytesMessage.getBodyLength());
                byte[] messageBytes = new byte[bodyLength];
                bytesMessage.readBytes(messageBytes);
                return messageBytes;
            }
        });
    }

    @Override
    public Map<String, Object> getMetadata() throws AbstractJMSException {
        // No try/catch needed! The getters already handle and wrap exceptions.
        return Map.of(
                "JMSMessageID", getJMSMessageID(),
                "JMSCorrelationID", getJMSCorrelationID(),
                "JMSTimestamp", getJMSTimestamp(),  // No need for String.valueOf() if the return type is long
                "JMSDestination", getJMSDestination()   // This returns an AbstractDestination, which is fine
        );
    }

    @Override
    public Enumeration<?> getPropertyNames() throws AbstractJMSException {
        return execute(msg -> isJakarta
                ? ((jakarta.jms.Message) msg).getPropertyNames()
                : ((javax.jms.Message) msg).getPropertyNames()
        );
    }

    @Override
    public Object getObjectProperty(String name) throws AbstractJMSException {
        return execute(msg -> isJakarta
                ? ((jakarta.jms.Message) msg).getObjectProperty(name)
                : ((javax.jms.Message) msg).getObjectProperty(name)
        );
    }

    @Override
    public String getStringProperty(String name) throws AbstractJMSException {
        return execute(msg -> isJakarta
                ? ((jakarta.jms.Message) msg).getStringProperty(name)
                : ((javax.jms.Message) msg).getStringProperty(name)
        );
    }

    @Override
    public Object getMessage() {
        return this.message;
    }

    @Override
    public Boolean isTextMessageInstance() throws AbstractJMSException {
        try {
            if (isJakarta) {
                if (message instanceof jakarta.jms.TextMessage) {
                    return true;
                }
            } else {
                if (message instanceof javax.jms.TextMessage) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e)
        {
            throw new AbstractJMSException("Failed to validate TextMessage instance", e);
        }
    }

    @Override
    public Boolean isBytesMessageInstance() throws AbstractJMSException {
        try {
            if (isJakarta) {
                return message instanceof jakarta.jms.BytesMessage;
            } else {
                return message instanceof javax.jms.BytesMessage;
            }
        }
        catch (Exception e)
        {
            throw new AbstractJMSException("Failed to validate BytesMessage instance", e);
        }
    }
}