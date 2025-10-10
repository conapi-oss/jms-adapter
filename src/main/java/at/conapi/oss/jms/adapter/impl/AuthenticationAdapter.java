package at.conapi.oss.jms.adapter.impl;

/**
 * Internal implementation: Authentication credentials holder.
 * <p>
 * A reusable, immutable record to hold endpoint-specific authentication credentials.
 * As a record, it's concise and provides getters, equals(), hashCode(), and toString() automatically.
 * </p>
 * <p>
 * <strong>Note:</strong> This is an internal implementation class and not part of the public API.
 * Users should not instantiate this class directly.
 * </p>
 *
 * @param userName the username for authentication
 * @param password the password for authentication
 * @since 1.0.0
 */
public record AuthenticationAdapter (String userName, String password) {}