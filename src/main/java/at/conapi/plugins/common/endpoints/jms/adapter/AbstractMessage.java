package at.conapi.plugins.common.endpoints.jms.adapter;

import java.util.Enumeration;
import java.util.Map;

/**
 * Vendor-neutral interface for JMS messages.
 * <p>
 * This interface provides unified access to message body, headers, and properties
 * for both text and bytes message types, supporting both javax.jms and jakarta.jms namespaces.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractMessage {

    // --- Body ---

    /**
     * Returns the message body as text.
     *
     * @return the text message body, or null if not a text message
     * @throws AbstractJMSException if the text cannot be retrieved
     * @since 1.0.0
     */
    String getText() throws AbstractJMSException;

    /**
     * Returns the message body as a byte array.
     *
     * @return the bytes message body, or null if not a bytes message
     * @throws AbstractJMSException if the bytes cannot be retrieved
     * @since 1.0.0
     */
    byte[] getByteArray() throws AbstractJMSException;

    /**
     * Clears the message body, allowing it to be rewritten.
     *
     * @throws AbstractJMSException if the body cannot be cleared
     * @since 1.0.0
     */
    void clearBody() throws AbstractJMSException;

    // --- Headers ---

    /**
     * Gets the message ID assigned by the JMS provider.
     *
     * @return the message ID
     * @throws AbstractJMSException if the ID cannot be retrieved
     * @since 1.0.0
     */
    String getJMSMessageID() throws AbstractJMSException;

    /**
     * Sets the message ID (typically used by JMS provider).
     *
     * @param id the message ID
     * @throws AbstractJMSException if the ID cannot be set
     * @since 1.0.0
     */
    void setJMSMessageID(String id) throws AbstractJMSException;

    /**
     * Gets the message timestamp (time the message was sent).
     *
     * @return the timestamp in milliseconds since epoch
     * @throws AbstractJMSException if the timestamp cannot be retrieved
     * @since 1.0.0
     */
    long getJMSTimestamp() throws AbstractJMSException;

    /**
     * Sets the message timestamp.
     *
     * @param timestamp the timestamp in milliseconds since epoch
     * @throws AbstractJMSException if the timestamp cannot be set
     * @since 1.0.0
     */
    void setJMSTimestamp(long timestamp) throws AbstractJMSException;

    /**
     * Gets the correlation ID for linking messages (e.g., request-reply).
     *
     * @return the correlation ID, or null if not set
     * @throws AbstractJMSException if the correlation ID cannot be retrieved
     * @since 1.0.0
     */
    String getJMSCorrelationID() throws AbstractJMSException;

    /**
     * Sets the correlation ID for linking messages.
     *
     * @param correlationID the correlation ID
     * @throws AbstractJMSException if the correlation ID cannot be set
     * @since 1.0.0
     */
    void setJMSCorrelationID(String correlationID) throws AbstractJMSException;

    /**
     * Gets the destination to which a reply should be sent.
     *
     * @return the reply-to destination, or null if not set
     * @throws AbstractJMSException if the reply-to cannot be retrieved
     * @since 1.0.0
     */
    AbstractDestination getJMSReplyTo() throws AbstractJMSException;

    /**
     * Sets the destination to which a reply should be sent.
     *
     * @param replyTo the reply-to destination
     * @throws AbstractJMSException if the reply-to cannot be set
     * @since 1.0.0
     */
    void setJMSReplyTo(AbstractDestination replyTo) throws AbstractJMSException;

    /**
     * Gets the destination to which the message was sent.
     *
     * @return the message destination
     * @throws AbstractJMSException if the destination cannot be retrieved
     * @since 1.0.0
     */
    AbstractDestination getJMSDestination() throws AbstractJMSException;

    /**
     * Sets the destination to which the message was sent.
     *
     * @param destination the message destination
     * @throws AbstractJMSException if the destination cannot be set
     * @since 1.0.0
     */
    void setJMSDestination(AbstractDestination destination) throws AbstractJMSException;

    /**
     * Gets the message delivery mode (persistent or non-persistent).
     *
     * @return the delivery mode
     * @throws AbstractJMSException if the delivery mode cannot be retrieved
     * @since 1.0.0
     */
    int getJMSDeliveryMode() throws AbstractJMSException;

    /**
     * Sets the message delivery mode.
     *
     * @param deliveryMode the delivery mode (persistent or non-persistent)
     * @throws AbstractJMSException if the delivery mode cannot be set
     * @since 1.0.0
     */
    void setJMSDeliveryMode(int deliveryMode) throws AbstractJMSException;

    /**
     * Checks if the message is being redelivered.
     *
     * @return true if the message is being redelivered, false otherwise
     * @throws AbstractJMSException if the status cannot be retrieved
     * @since 1.0.0
     */
    boolean getJMSRedelivered() throws AbstractJMSException;

    /**
     * Sets the redelivered flag.
     *
     * @param redelivered true if the message is being redelivered
     * @throws AbstractJMSException if the flag cannot be set
     * @since 1.0.0
     */
    void setJMSRedelivered(boolean redelivered) throws AbstractJMSException;

    /**
     * Gets the message type identifier set by the application.
     *
     * @return the message type, or null if not set
     * @throws AbstractJMSException if the type cannot be retrieved
     * @since 1.0.0
     */
    String getJMSType() throws AbstractJMSException;

    /**
     * Sets the message type identifier.
     *
     * @param type the message type
     * @throws AbstractJMSException if the type cannot be set
     * @since 1.0.0
     */
    void setJMSType(String type) throws AbstractJMSException;

    /**
     * Gets the message expiration time.
     *
     * @return the expiration time in milliseconds since epoch, or 0 if the message does not expire
     * @throws AbstractJMSException if the expiration cannot be retrieved
     * @since 1.0.0
     */
    long getJMSExpiration() throws AbstractJMSException;

    /**
     * Sets the message expiration time.
     *
     * @param expiration the expiration time in milliseconds since epoch, or 0 for no expiration
     * @throws AbstractJMSException if the expiration cannot be set
     * @since 1.0.0
     */
    void setJMSExpiration(long expiration) throws AbstractJMSException;

    /**
     * Gets the earliest time the message should be delivered.
     *
     * @return the delivery time in milliseconds since epoch
     * @throws AbstractJMSException if the delivery time cannot be retrieved
     * @since 1.0.0
     */
    long getJMSDeliveryTime() throws AbstractJMSException;

    /**
     * Sets the earliest time the message should be delivered.
     *
     * @param deliveryTime the delivery time in milliseconds since epoch
     * @throws AbstractJMSException if the delivery time cannot be set
     * @since 1.0.0
     */
    void setJMSDeliveryTime(long deliveryTime) throws AbstractJMSException;

    /**
     * Gets the message priority.
     *
     * @return the priority (0-9, where 0 is lowest and 9 is highest)
     * @throws AbstractJMSException if the priority cannot be retrieved
     * @since 1.0.0
     */
    int getJMSPriority() throws AbstractJMSException;

    /**
     * Sets the message priority.
     *
     * @param priority the priority (0-9, where 0 is lowest and 9 is highest)
     * @throws AbstractJMSException if the priority cannot be set
     * @since 1.0.0
     */
    void setJMSPriority(int priority) throws AbstractJMSException;

    // --- Properties ---

    /**
     * Gets all message properties as a map.
     *
     * @return map of property names to values
     * @throws AbstractJMSException if properties cannot be retrieved
     * @since 1.0.0
     */
    Map<String, Object> getProperties() throws AbstractJMSException;

    /**
     * Gets an enumeration of all property names.
     *
     * @return enumeration of property names
     * @throws AbstractJMSException if property names cannot be retrieved
     * @since 1.0.0
     */
    Enumeration<?> getPropertyNames() throws AbstractJMSException;

    /**
     * Gets a property value as an Object.
     *
     * @param name the property name
     * @return the property value, or null if not set
     * @throws AbstractJMSException if the property cannot be retrieved
     * @since 1.0.0
     */
    Object getObjectProperty(String name) throws AbstractJMSException;

    /**
     * Gets a property value as a String.
     *
     * @param name the property name
     * @return the property value as a String, or null if not set
     * @throws AbstractJMSException if the property cannot be retrieved
     * @since 1.0.0
     */
    String getStringProperty(String name) throws AbstractJMSException;

    /**
     * Clears all message properties.
     *
     * @throws AbstractJMSException if properties cannot be cleared
     * @since 1.0.0
     */
    void clearProperties() throws AbstractJMSException;

    // --- Control ---

    /**
     * Acknowledges receipt of this message.
     * <p>
     * This method is used in client-acknowledge mode to acknowledge message delivery.
     * </p>
     *
     * @throws AbstractJMSException if the message cannot be acknowledged
     * @since 1.0.0
     */
    void acknowledge() throws AbstractJMSException;

    // --- Utility ---

    /**
     * Returns the underlying vendor-specific message object.
     * <p>
     * This method provides access to the wrapped JMS message for advanced use cases
     * requiring vendor-specific functionality.
     * </p>
     *
     * @return the underlying message object
     * @since 1.0.0
     */
    Object getMessage();

    /**
     * Gets additional metadata about the message.
     *
     * @return map of metadata key-value pairs
     * @throws AbstractJMSException if metadata cannot be retrieved
     * @since 1.0.0
     */
    Map<String, Object> getMetadata() throws AbstractJMSException;

    // --- Checks ---

    /**
     * Checks if this is a text message.
     *
     * @return true if this is a text message, false otherwise
     * @throws AbstractJMSException if the check fails
     * @since 1.0.0
     */
    Boolean isTextMessageInstance() throws AbstractJMSException;

    /**
     * Checks if this is a bytes message.
     *
     * @return true if this is a bytes message, false otherwise
     * @throws AbstractJMSException if the check fails
     * @since 1.0.0
     */
    Boolean isBytesMessageInstance() throws AbstractJMSException;
}