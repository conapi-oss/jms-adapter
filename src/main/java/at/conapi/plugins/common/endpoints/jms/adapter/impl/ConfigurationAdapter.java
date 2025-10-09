package at.conapi.plugins.common.endpoints.jms.adapter.impl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A platform-agnostic, reusable model for shared JMS configuration.
 * This class has no dependency on any specific gateway or framework.
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

    // A common interface for entities that provide a destination URL.
    private interface DestinationProvider {
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

    @Data
    public static class Producer implements DestinationProvider {
        private boolean enabled;
        private DestinationConfig destination;

        public String getDestinationUrl() {
            return getDestinationUrl(destination);
        }
    }

    @Data
    public static class Consumer implements DestinationProvider {
        private boolean enabled;
        private DestinationConfig destination;

        public String getDestinationUrl() {
            return getDestinationUrl(destination);
        }
    }

    /**
     * Using a record for immutable, simple data structures.
     */
    public record DestinationConfig(String type, String destination) {}
    public record KeyValuePair(String key, String value) {}

    /**
     * Base class for connection settings, containing shared properties.
     */
    @Data
    public abstract static class ConnectionSettings {
        private List<KeyValuePair> properties;
        private String jmsLibsPath;

        public Map<String, String> getPropertiesAsMap() {
            if (properties == null) {
                return Map.of();
            }
            return properties.stream()
                    .collect(Collectors.toMap(KeyValuePair::key, KeyValuePair::value));
        }

        @Data
        @EqualsAndHashCode(callSuper = true)
        public static class Direct extends ConnectionSettings {
            private String connectionFactoryClass;
            private String userName;
            private String password;
        }

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