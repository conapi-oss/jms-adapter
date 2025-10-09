package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractDestination;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;

public class DestinationAdapter implements AbstractDestination {

    private final Object destination;
    private final boolean isJakarta;

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