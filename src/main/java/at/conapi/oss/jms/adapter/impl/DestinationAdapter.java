package at.conapi.oss.jms.adapter.impl;

import at.conapi.oss.jms.adapter.AbstractDestination;
import at.conapi.oss.jms.adapter.AbstractJMSException;

/**
 * Internal implementation: Adapter wrapping vendor-specific JMS Destination.
 * <p>
 * This class wraps either a javax.jms.Destination or jakarta.jms.Destination
 * and delegates all operations to the underlying implementation.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
public class DestinationAdapter implements AbstractDestination {

    private final Object destination;
    private final boolean isJakarta;

    /**
     * Constructs a DestinationAdapter wrapping a vendor-specific destination.
     *
     * @param destination the underlying javax or jakarta JMS destination
     * @since 1.0.0
     */
    public DestinationAdapter(Object destination) {
        this.destination = destination;
        this.isJakarta = destination instanceof jakarta.jms.Destination;
    }

    @Override
    public Object getDestination() {
        return destination;
    }

    @Override
    public String getDestinationName() throws AbstractJMSException {
        try {
            if (this.isJakarta) {
                if (((jakarta.jms.Destination) destination) instanceof jakarta.jms.Queue) {
                    return ((jakarta.jms.Queue) destination).getQueueName();
                }  else if (((jakarta.jms.Destination) destination) instanceof jakarta.jms.Topic) {
                    return ((jakarta.jms.Topic) destination).getTopicName();
                } else {
                    throw new AbstractJMSException("Unknown Destination Type for jakarta Destination");
                }
            } else {
                if (((javax.jms.Destination) destination) instanceof javax.jms.Queue) {
                    return ((javax.jms.Queue) destination).getQueueName();
                }  else if (((javax.jms.Destination) destination) instanceof javax.jms.Topic) {
                    return ((javax.jms.Topic) destination).getTopicName();
                } else {
                    throw new AbstractJMSException("Unknown Destination Type for javax Destination");
                }
            }
        }
        catch (Exception e)
        {
            throw new AbstractJMSException("Problem executing getDestinationName()", e);
        }
    }

    @Override
    public Boolean isTemporaryDestination() throws AbstractJMSException {
        try {
            if (this.isJakarta) {
                if (((jakarta.jms.Destination) destination) instanceof jakarta.jms.TemporaryQueue) {
                    return true;
                }  else
                    return ((jakarta.jms.Destination) destination) instanceof jakarta.jms.TemporaryTopic;
            } else {
                if (((javax.jms.Destination) destination) instanceof javax.jms.TemporaryQueue) {
                    return true;
                }  else
                    return ((javax.jms.Destination) destination) instanceof javax.jms.TemporaryTopic;
            }
        }
        catch (Exception e)
        {
            throw new AbstractJMSException("Problem executing isTemporaryDestination()", e);
        }
    }

    @Override
    public DestinationType getDestinationType() throws AbstractJMSException {
        try {
            if (this.isJakarta) {
                if (((jakarta.jms.Destination) destination) instanceof jakarta.jms.Queue) {
                    return DestinationType.QUEUE;
                }  else if (((jakarta.jms.Destination) destination) instanceof jakarta.jms.Topic) {
                    return DestinationType.TOPIC;
                } else {
                    throw new AbstractJMSException("Unknown Destination Type for jakarta Destination");
                }
            } else {
                if (((javax.jms.Destination) destination) instanceof javax.jms.Queue) {
                    return DestinationType.QUEUE;
                }  else if (((javax.jms.Destination) destination) instanceof javax.jms.Topic) {
                    return DestinationType.TOPIC;
                } else {
                    throw new AbstractJMSException("Unknown Destination Type for javax Destination");
                }
            }
        }
        catch (Exception e)
        {
            throw new AbstractJMSException("Problem executing isTemporaryDestination()", e);
        }
    }
}