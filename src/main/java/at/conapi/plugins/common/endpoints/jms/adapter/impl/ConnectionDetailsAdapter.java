package at.conapi.plugins.common.endpoints.jms.adapter.impl;

/**
 * Internal implementation: Connection details holder.
 * <p>
 * Immutable record containing connection endpoint details and credentials.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @param host the hostname or IP address
 * @param port the port number
 * @param username the username for authentication
 * @param password the password for authentication
 * @since 1.0.0
 */
public record ConnectionDetailsAdapter(String host, String port, String username, String password) {}
