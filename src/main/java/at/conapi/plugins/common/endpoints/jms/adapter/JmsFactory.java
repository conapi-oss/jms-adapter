package at.conapi.plugins.common.endpoints.jms.adapter;

import at.conapi.plugins.common.endpoints.jms.adapter.impl.ConnectionFactoryAdapter;
import at.conapi.plugins.common.endpoints.jms.adapter.impl.DestinationAdapter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Factory for creating vendor-neutral JMS components with dual javax/jakarta namespace support.
 * <p>
 * This factory enables unified access to JMS 1.x+ compliant message brokers through a dynamic adapter
 * pattern. It supports loading JMS provider libraries at runtime and automatically handles both
 * javax.jms and jakarta.jms namespaces.
 * </p>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Creating ConnectionFactory via JNDI</h3>
 * <pre>{@code
 * // Initialize factory with provider JARs
 * JmsFactory factory = new JmsFactory("/path/to/provider/jars");
 *
 * // Lookup connection factory via JNDI
 * Hashtable<String, String> jndiProps = new Hashtable<>();
 * jndiProps.put("java.naming.factory.initial", "com.example.JndiContextFactory");
 * jndiProps.put("java.naming.provider.url", "tcp://localhost:61616");
 *
 * AbstractConnectionFactory cf = factory.lookupConnectionFactory(jndiProps, "ConnectionFactory");
 * }</pre>
 *
 * <h3>Creating ConnectionFactory Directly</h3>
 * <pre>{@code
 * // Create factory with provider JARs
 * JmsFactory factory = new JmsFactory("/path/to/provider/jars");
 *
 * // Create connection factory by class name
 * Map<String, String> props = new HashMap<>();
 * props.put("brokerURL", "tcp://localhost:61616");
 *
 * AbstractConnectionFactory cf = factory.createConnectionFactory(
 *     "com.example.ConnectionFactoryImpl",
 *     props
 * );
 * }</pre>
 *
 * @since 1.0.0
 */
public class JmsFactory {

    private final ClassLoader providerClassLoader;
    private Hashtable<String, String> jndiProperties=null;

    /**
     * Creates a JmsFactory using the current classloader.
     * <p>
     * This constructor is suitable when JMS provider libraries are already available
     * on the application's classpath.
     * </p>
     *
     * @since 1.0.0
     */
    public JmsFactory() {
        this(new URL[0]);
    }

    /**
     * Creates a JmsFactory with specific provider JAR files.
     * <p>
     * This constructor allows loading JMS provider libraries from specific JAR files
     * at runtime, isolating them from the application's classpath.
     * </p>
     *
     * @param providerJars array of URLs pointing to JMS provider JAR files, or empty array to use current classloader
     * @since 1.0.0
     */
    public JmsFactory(URL[] providerJars) {
        if (providerJars == null || providerJars.length == 0) {
            this.providerClassLoader = this.getClass().getClassLoader();
        }
        else {
            this.providerClassLoader = new URLClassLoader(providerJars, this.getClass().getClassLoader().getParent());
        }
    }

    /**
     * Creates a JmsFactory by loading all JAR files from a directory path.
     * <p>
     * This constructor recursively walks the specified directory and loads all JAR files
     * into an isolated classloader.
     * </p>
     *
     * @param providerJarPath path to directory containing JMS provider JAR files, or null/empty to use current classloader
     * @throws AbstractJMSException if the directory cannot be read or JAR files cannot be loaded
     * @since 1.0.0
     */
    public JmsFactory(String providerJarPath) throws AbstractJMSException {
        try {
            if (providerJarPath == null || providerJarPath.isEmpty()) {
                this.providerClassLoader = this.getClass().getClassLoader();
            } else {
                List<URL> urls = new ArrayList<>();
                Path path = Paths.get(providerJarPath);

                Files.walk(path)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                URL url = file.toUri().toURL();
                                urls.add(url);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        });
                this.providerClassLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader().getParent());
            }
        } catch (IOException e) {
            throw new AbstractJMSException("Failed to create JMS factory", e);
        }

    }

    /**
     * Looks up a JMS ConnectionFactory from JNDI.
     * <p>
     * This method performs a JNDI lookup using the provider's naming context, automatically
     * handling both javax.naming and jakarta.naming namespaces. The JNDI properties are cached
     * for subsequent {@link #lookupDestination(Hashtable, String)} calls.
     * </p>
     *
     * @param jndiProperties JNDI context properties (e.g., java.naming.factory.initial, java.naming.provider.url)
     * @param factoryName JNDI name of the ConnectionFactory to lookup
     * @return vendor-neutral ConnectionFactory adapter
     * @throws AbstractJMSException if JNDI lookup fails or ConnectionFactory cannot be found
     * @since 1.0.0
     */
    public AbstractConnectionFactory lookupConnectionFactory(Hashtable<String, String> jndiProperties, String factoryName) throws AbstractJMSException {
        try {
            // save the JNDI properties for later use
            this.jndiProperties = jndiProperties;
            return new ConnectionFactoryAdapter(jndiLookup(jndiProperties, factoryName));
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to lookup ConnectionFactory", e);
        }
    }

    /**
     * Looks up a JMS Destination (Queue or Topic) from JNDI.
     * <p>
     * This method performs a JNDI lookup using the provider's naming context. If jndiProperties
     * is null, it uses the properties from the previous {@link #lookupConnectionFactory(Hashtable, String)} call.
     * Automatically handles both javax.naming and jakarta.naming namespaces.
     * </p>
     *
     * @param jndiProperties JNDI context properties, or null to reuse cached properties from ConnectionFactory lookup
     * @param destinationName JNDI name of the Destination (Queue or Topic) to lookup
     * @return vendor-neutral Destination adapter
     * @throws AbstractJMSException if JNDI lookup fails or Destination cannot be found
     * @since 1.0.0
     */
    public AbstractDestination lookupDestination(Hashtable<String, String> jndiProperties, String destinationName) throws AbstractJMSException {

        try {
            if(jndiProperties == null) {
                // use the ones from the factory
                jndiProperties = this.jndiProperties;
            }
            return new DestinationAdapter(jndiLookup(jndiProperties, destinationName));
        } catch (Exception e) {
            throw new AbstractJMSException("Failed to lookup Destination", e);
        }
    }

    private Object jndiLookup(Hashtable<String, String> jndiProperties, final String lookupName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(providerClassLoader);
        try {
            Class<?> contextClass = providerClassLoader.loadClass("javax.naming.InitialContext");
            if (contextClass == null) {
                contextClass = providerClassLoader.loadClass("jakarta.naming.InitialContext");
            }

            // create the context
            Object context = contextClass.getConstructor(Hashtable.class).newInstance(jndiProperties);

            // lookup the object
            Method lookupMethod = contextClass.getMethod("lookup", String.class);
            Object jndiObject = lookupMethod.invoke(context, lookupName);

            // close the context
            contextClass.getMethod("close").invoke(context);

            return jndiObject;
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    /**
     * Creates a JMS ConnectionFactory by instantiating a provider-specific class directly.
     * <p>
     * This method loads the specified ConnectionFactory class from the provider's classloader,
     * instantiates it using the default constructor, and configures it using reflection-based
     * property setters. This approach is useful when JNDI is not available or desired.
     * </p>
     * <p>
     * Properties are set using JavaBean-style setter methods (e.g., "brokerURL" → setBrokerURL()).
     * See {@link GenericPropertySetter} for supported property types.
     * </p>
     *
     * @param className fully qualified class name of the provider's ConnectionFactory implementation
     * @param props configuration properties to set on the ConnectionFactory instance
     * @return vendor-neutral ConnectionFactory adapter
     * @throws AbstractJMSException if class cannot be loaded, instantiated, or configured
     * @since 1.0.0
     */
    public AbstractConnectionFactory createConnectionFactory(String className, Map<String, String> props) throws AbstractJMSException {
        try {
            Class<?> cfClass = providerClassLoader.loadClass(className.trim());
            Object connectionFactory = cfClass.getDeclaredConstructor().newInstance();
            GenericPropertySetter.setProperties(connectionFactory, props);

            return new ConnectionFactoryAdapter(connectionFactory);

        } catch (ClassNotFoundException e) {
            // First, print a clear error message to the log
            System.err.println("\n!!! FAILED TO FIND CLASS: " + className + " !!!");

            // Now, dump the details of the specific classloader that failed
            dumpClassLoaderInfo(providerClassLoader, className);

            // Finally, throw your exception so the process fails as expected
            throw new AbstractJMSException("Failed to create ConnectionFactory because class '" + className + "' was not found.", e);

        } catch (Exception e) {
            // Catch other potential reflection/instantiation errors
            throw new AbstractJMSException("Failed to create ConnectionFactory, an unexpected error occurred.", e);
        }
    }

    /**
     * A utility method to dump diagnostic information about a specific classloader.
     * @param classLoader The classloader to inspect.
     * @param className The name of the class that could not be found.
     */
    private void dumpClassLoaderInfo(ClassLoader classLoader, String className) {
        System.out.println("--- STARTING CLASSLOADER DUMP for class: " + className + " ---");
        try {
            System.out.println("Inspecting ClassLoader: " + classLoader);

            // Print the classloader hierarchy
            int level = 0;
            ClassLoader current = classLoader;
            while (current != null) {
                String indent = "  ".repeat(level++);
                System.out.println(indent + "-> " + current);

                // If it's a URLClassLoader, print its search paths (JARs and directories)
                if (current instanceof URLClassLoader) {
                    URL[] urls = ((URLClassLoader) current).getURLs();
                    if (urls.length == 0) {
                        System.out.println(indent + "   (No URLs found for this classloader)");
                    }
                    for (URL url : urls) {
                        System.out.println(indent + "   - " + url.getFile());
                    }
                }
                current = current.getParent();
            }
        } catch (Exception e) {
            System.err.println("Failed to dump classloader info: " + e.getMessage());
        }
        System.out.println("--- FINISHED CLASSLOADER DUMP ---");
    }
}