package at.conapi.plugins.common.endpoints.jms.adapter.impl;

/**
 * A reusable, immutable record to hold endpoint-specific authentication credentials.
 * As a record, it's concise and provides getters, equals(), hashCode(), and toString() automatically.
 */
public record AuthenticationAdapter (String userName, String password) {}