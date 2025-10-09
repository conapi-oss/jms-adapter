package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractMessage;
import at.conapi.plugins.common.endpoints.jms.adapter.AbstractProducer;

public class ProducerAdapter implements AbstractProducer {
    private final Object producer;
    private final boolean isJakarta;
    public ProducerAdapter(Object producer) {
        this.producer = producer;
        this.isJakarta = producer instanceof jakarta.jms.MessageProducer;
    }

    @Override
    public void send(AbstractMessage message) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageProducer) producer).send((jakarta.jms.Message) message.getMessage());
            } else {
                ((javax.jms.MessageProducer) producer).send((javax.jms.Message) message.getMessage());
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to send message", e);
        }
    }

    @Override
    public void send(AbstractMessage message, int deliveryMode) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageProducer) producer).send(
                        (jakarta.jms.Message) message.getMessage(),
                        deliveryMode,
                        ((jakarta.jms.MessageProducer) producer).getPriority(),
                        ((jakarta.jms.MessageProducer) producer).getTimeToLive());

            } else {
                ((javax.jms.MessageProducer) producer).send(
                        (javax.jms.Message) message.getMessage(),
                        deliveryMode,
                        ((javax.jms.MessageProducer) producer).getPriority(),
                        ((javax.jms.MessageProducer) producer).getTimeToLive());

            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to send message", e);
        }
    }

    @Override
    public void send(AbstractMessage message, int deliveryMode, int priority) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageProducer) producer).send(
                        (jakarta.jms.Message) message.getMessage(),
                        deliveryMode,
                        priority,
                        ((jakarta.jms.MessageProducer) producer).getTimeToLive());

            } else {
                ((javax.jms.MessageProducer) producer).send(
                        (javax.jms.Message) message.getMessage(),
                        deliveryMode,
                        priority,
                        ((javax.jms.MessageProducer) producer).getTimeToLive());

            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to send message", e);
        }
    }

    @Override
    public void send(AbstractMessage message, int deliveryMode, int priority, long timeToLive) throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageProducer) producer).send(
                        (jakarta.jms.Message) message.getMessage(),
                        deliveryMode,
                        priority,
                        timeToLive);
            } else {
                ((javax.jms.MessageProducer) producer).send(
                        (javax.jms.Message) message.getMessage(),
                        deliveryMode,
                        priority,
                        timeToLive);
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to send message", e);
        }
    }

    @Override
    public void close() throws AbstractJMSException {
        try {
            if (isJakarta) {
                ((jakarta.jms.MessageProducer) producer).close();
            } else {
                ((javax.jms.MessageProducer) producer).close();
            }
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to close producer", e);
        }
    }
}
