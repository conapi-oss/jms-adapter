package at.conapi.oss.jms.adapter.impl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Internal implementation: Platform-agnostic JMS configuration model.
 * <p>
 * A platform-agnostic, reusable model for shared JMS configuration.
 * This class has no dependency on any specific gateway or framework.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @since 1.0.0
 */
@Data
public class ConfigurationAdapter {

    private Producer producer;
    private Consumer consumer;

    /**
     * Polymorphic representation of connection settings.
     * The @JsonTypeInfo annotations are necessary for libraries like Jackson
     * to correctly deserialize the JSON configuration into either Direct or Jndi settings.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ConnectionSettings.Direct.class, name = "DIRECT"),
            @JsonSubTypes.Type(value = ConnectionSettings.Jndi.class, name = "JNDI")
    })
    private ConnectionSettings connection;

    /**
     * Internal interface for entities that provide a destination URL.
     *
     * @since 1.0.0
     */
    private interface DestinationProvider {

        /**
         * Converts a destination configuration to a URL string.
         *
         * @param destinationConfig the destination configuration
         * @return the destination URL string, or null if config is invalid
         * @since 1.0.0
         */
        default String getDestinationUrl(final DestinationConfig destinationConfig) {
            if (destinationConfig == null || destinationConfig.type() == null) {
                return null;
            }
            final String type = destinationConfig.type().toLowerCase();
            return switch (type) {
                case "topic", "queue", "jndi" -> type + "://" + destinationConfig.destination();
                default -> throw new IllegalArgumentException("Unknown destination type: " + destinationConfig.type());
            };
        }
    }

    /**
     * Producer configuration settings.
     *
     * @since 1.0.0
     */
    @Data
    public static class Producer implements DestinationProvider {
        private boolean enabled;
        private DestinationConfig destination;

        /**
         * Gets the destination URL for this producer.
         *
         * @return the destination URL string
         * @since 1.0.0
         */
        public String getDestinationUrl() {
            return getDestinationUrl(destination);
        }
    }

    /**
     * Consumer configuration settings.
     *
     * @since 1.0.0
     */
    @Data
    public static class Consumer implements DestinationProvider {
        private boolean enabled;
        private DestinationConfig destination;

        /**
         * Gets the destination URL for this consumer.
         *
         * @return the destination URL string
         * @since 1.0.0
         */
        public String getDestinationUrl() {
            return getDestinationUrl(destination);
        }
    }

    /**
     * Destination configuration record.
     *
     * @param type the destination type (queue, topic, or jndi)
     * @param destination the destination name or path
     * @since 1.0.0
     */
    public record DestinationConfig(String type, String destination) {}

    /**
     * Key-value pair record for properties.
     *
     * @param key the property key
     * @param value the property value
     * @since 1.0.0
     */
    public record KeyValuePair(String key, String value) {}

    /**
     * Base class for connection settings, containing shared properties.
     *
     * @since 1.0.0
     */
    @Data
    public abstract static class ConnectionSettings {
        private List<KeyValuePair> properties;
        private String jmsLibsPath;

        /**
         * Converts the properties list to a Map.
         *
         * @return map of property keys to values
         * @since 1.0.0
         */
        public Map<String, String> getPropertiesAsMap() {
            if (properties == null) {
                return Map.of();
            }
            return properties.stream()
                    .collect(Collectors.toMap(KeyValuePair::key, KeyValuePair::value));
        }

        /**
         * Direct connection settings without JNDI.
         *
         * @since 1.0.0
         */
        @Data
        @EqualsAndHashCode(callSuper = true)
        public static class Direct extends ConnectionSettings {
            private String connectionFactoryClass;
            private String userName;
            private String password;
        }

        /**
         * JNDI-based connection settings.
         *
         * @since 1.0.0
         */
        @Data
        @EqualsAndHashCode(callSuper = true)
        public static class Jndi extends ConnectionSettings {
            private String contextFactoryClass;
            private String providerUrl;
            private String principal;
            private String credentials;
            private String factoryName;
        }
    }
}