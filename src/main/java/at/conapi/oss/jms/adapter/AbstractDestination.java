package at.conapi.oss.jms.adapter;

/**
 * Vendor-neutral interface for JMS destinations (Queues and Topics).
 * <p>
 * A Destination represents either a Queue (point-to-point) or Topic (publish-subscribe)
 * messaging destination. This interface provides methods to query destination properties
 * in a vendor-neutral manner.
 * </p>
 *
 * @since 1.0.0
 */
public interface AbstractDestination {

    /**
     * Enumeration of JMS destination types.
     *
     * @since 1.0.0
     */
    public enum DestinationType {
        /** Point-to-point queue destination */
        QUEUE,
        /** Publish-subscribe topic destination */
        TOPIC
    }

    /**
     * Returns the underlying vendor-specific destination object.
     * <p>
     * This method provides access to the wrapped JMS destination for advanced use cases
     * requiring vendor-specific functionality.
     * </p>
     *
     * @return the underlying destination object
     * @since 1.0.0
     */
    public Object getDestination();

    /**
     * Returns the destination name.
     *
     * @return the destination name
     * @throws AbstractJMSException if the name cannot be retrieved
     * @since 1.0.0
     */
    public String getDestinationName() throws AbstractJMSException;

    /**
     * Returns the destination type (Queue or Topic).
     *
     * @return the destination type
     * @throws AbstractJMSException if the type cannot be determined
     * @since 1.0.0
     */
    public DestinationType getDestinationType() throws AbstractJMSException;

    /**
     * Checks if this is a temporary destination.
     * <p>
     * Temporary destinations are automatically deleted when the connection is closed.
     * </p>
     *
     * @return true if this is a temporary destination, false otherwise
     * @throws AbstractJMSException if the check fails
     * @since 1.0.0
     */
    public Boolean isTemporaryDestination() throws AbstractJMSException;
}
