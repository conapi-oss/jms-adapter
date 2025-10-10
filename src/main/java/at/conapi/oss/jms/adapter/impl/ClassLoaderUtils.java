package at.conapi.oss.jms.adapter.impl;

import java.util.concurrent.Callable;

/**
 * Utility class for managing thread context classloader during JMS operations.
 * <p>
 * This is necessary because some JMS providers create worker threads that use
 * Thread.currentThread().getContextClassLoader() for class loading operations
 * (e.g., during message deserialization). By setting the provider classloader
 * as the thread context classloader during JMS API calls, any threads created
 * by the provider will inherit the correct classloader.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal utility class and not part of the public API.
 * </p>
 *
 * @since 1.0.0
 */
class ClassLoaderUtils {

    /**
     * Executes an action with a specific classloader set as the thread context classloader.
     * The original classloader is restored after execution, even if an exception occurs.
     *
     * @param classLoader the classloader to set as thread context
     * @param action the action to execute
     * @param <T> the return type
     * @return the result of the action
     * @throws Exception if the action throws an exception
     */
    static <T> T withContextClassLoader(ClassLoader classLoader, Callable<T> action) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return action.call();
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    /**
     * Executes a void action with a specific classloader set as the thread context classloader.
     * The original classloader is restored after execution, even if an exception occurs.
     *
     * @param classLoader the classloader to set as thread context
     * @param action the action to execute
     * @throws Exception if the action throws an exception
     */
    static void withContextClassLoader(ClassLoader classLoader, RunnableWithException action) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            action.run();
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    /**
     * Functional interface for void operations that may throw checked exceptions.
     */
    @FunctionalInterface
    interface RunnableWithException {
        void run() throws Exception;
    }
}
