package at.conapi.plugins.common.endpoints.jms.adapter;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A custom classloader that enforces parent-first delegation for JMS API packages.
 * <p>
 * This classloader ensures that JMS API classes (javax.jms.* and jakarta.jms.*) are always
 * loaded from the parent classloader (i.e. the plugin classLoader), preventing
 * ClassCastException when vendor JARs bundle their own copies of the JMS API.
 *
 *   What happens:
 *   plugin class loader (has jms-adapter + JMS API)
 *         ↑ parent
 *   FilteredClassLoader (vendor JARs - INCLUDING bundled JMS API)
 *
 *   - Vendor JARs still contain javax.jms.* classes physically
 *   - FilteredClassLoader just refuses to load them, always delegating to parent
 *   - Those classes exist in the JAR but are never used
 *
 * </p>
 * <p>
 * All other classes follow the standard parent-delegation model, allowing vendor-specific
 * implementations to be loaded from the child classloader.
 * </p>
 *
 * @since 1.0.0
 */
class FilteredClassLoader extends URLClassLoader {

    /**
     * Package prefixes that must always be loaded from the parent classloader.
     */
    private static final String[] PARENT_FIRST_PACKAGES = {
        "javax.jms.",
        "jakarta.jms."
    };

    /**
     * Creates a new FilteredClassLoader with the specified URLs and parent classloader.
     *
     * @param urls the URLs from which to load classes and resources (vendor JARs)
     * @param parent the parent classloader (should be the PluginClassLoader)
     */
    public FilteredClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Loads the class with the specified name, enforcing parent-first delegation for JMS packages.
     * <p>
     * For JMS API packages (javax.jms.* and jakarta.jms.*), this method forces delegation
     * to the parent classloader, ignoring any classes bundled in vendor JARs.
     * </p>
     * <p>
     * For all other packages, the standard parent-delegation model is used.
     * </p>
     *
     * @param name the fully qualified name of the class
     * @param resolve if true, resolve the class
     * @return the resulting Class object
     * @throws ClassNotFoundException if the class could not be found
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Check if this class belongs to a JMS API package
        for (String pkg : PARENT_FIRST_PACKAGES) {
            if (name.startsWith(pkg)) {
                // Force parent classloader for JMS API classes
                // This prevents loading JMS API classes from vendor JARs
                ClassLoader parent = getParent();
                if (parent != null) {
                    return parent.loadClass(name);
                }
            }
        }

        // For all other classes, use the standard delegation model
        return super.loadClass(name, resolve);
    }
}