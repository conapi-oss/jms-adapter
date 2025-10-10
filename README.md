# jms-adapter

A lightweight, vendor-neutral Java library providing unified access to JMS 1.x+ compliant message brokers through dynamic adapter pattern and dual javax/jakarta namespace support.

## Features

- **Vendor-Neutral**: Works with any JMS 1.x+ compliant broker (ActiveMQ, IBM MQ, RabbitMQ, etc.)
- **Dual Namespace Support**: Seamlessly supports both `javax.jms` and `jakarta.jms` namespaces
- **Dynamic Class Loading**: Load JMS provider libraries at runtime without compile-time dependencies
- **Unified API**: Single consistent interface across different JMS implementations
- **JNDI Support**: Create connections via JNDI or direct instantiation

## Installation

### Maven

```xml
<dependency>
    <groupId>at.conapi.oss</groupId>
    <artifactId>jms-adapter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'at.conapi.oss:jms-adapter:1.0.0'
```

## Usage

### Basic Example - Direct Connection

```java
import at.conapi.oss.jms.adapter.*;

// Initialize factory with path to JMS provider JARs
JmsFactory factory = new JmsFactory("/path/to/provider/jars");

        // Create connection factory
        Map<String, String> props = Map.of(
                "brokerURL", "tcp://localhost:61616"
        );
        AbstractConnectionFactory cf = factory.createConnectionFactory(
                "org.apache.activemq.ActiveMQConnectionFactory",
                props
        );

        // Create connection and session
        AbstractConnection connection = cf.createConnection("username", "password");
connection.

        start();

        AbstractSession session = connection.createSession(false, 1);

        // Create producer and send message
        AbstractDestination destination = session.createDestination("queue://myQueue");
        AbstractProducer producer = session.createProducer(destination);
        AbstractMessage message = session.createTextMessage("Hello World", null);
producer.

        send(message);

// Cleanup
producer.

        close();
session.

        close();
connection.

        close();
```

### JNDI Lookup Example

```java
Hashtable<String, String> jndiProps = new Hashtable<>();
jndiProps.put("java.naming.factory.initial", "com.example.JndiContextFactory");
jndiProps.put("java.naming.provider.url", "tcp://localhost:61616");

AbstractConnectionFactory cf = factory.lookupConnectionFactory(jndiProps, "ConnectionFactory");
```

## Building from Source

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- GPG key for signing (if releasing)

### Build

```bash
# Clone repository
git clone https://github.com/conapi-oss/jms-adapter.git
cd jms-adapter

# Build and install locally
mvn clean install
```

## Release Process (for Maintainers)

### Prerequisites

1. **GPG Key Setup**:
   ```bash
   # Generate key if needed
   gpg --gen-key

   # Publish to keyserver
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

   # Fix GPG TTY issue (add to ~/.bashrc or ~/.zshrc)
   export GPG_TTY=$(tty)
   ```

2. **Maven Central Credentials** in `~/.m2/settings.xml`:
   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>YOUR_TOKEN_USERNAME</username>
         <password>YOUR_TOKEN_PASSWORD</password>
       </server>
       <server>
         <id>gpg.passphrase</id>
         <passphrase>YOUR_GPG_PASSPHRASE</passphrase>
       </server>
     </servers>
   </settings>
   ```

### Release Commands

```bash
# Ensure clean working directory
git status

# Prepare release (creates tag, updates versions)
mvn release:prepare

# Perform release (builds, signs, deploys to Maven Central)
mvn release:perform
```

The release plugin will:
- Update version from `x.x.x-SNAPSHOT` to `x.x.x`
- Create and push git tag `vx.x.x`
- Build and sign artifacts
- Deploy to Maven Central
- Update to next SNAPSHOT version

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions:
- GitHub Issues: https://github.com/conapi-oss/jms-adapter/issues
- Website: https://conapi.at
