package net.nemerosa.ontrack.model.support;

/**
 * This service is used to log messages at application level, to be seen by operation and administration people.
 * <p>
 * Having a message here would mean that something is defective in the application or in its configuration.
 */
public interface ApplicationLogService {

    /**
     * List of messages
     */
    ApplicationLogEntries getLogEntries(Page page);

    /**
     * Logs an error.
     *
     * @param exception  Exception associated with this error, can be null
     * @param source     General source of the error (cannot be null)
     * @param identifier Identifier within the source (might be null)
     * @param context    Descriptive message for the source
     * @param info       Additional information
     */
    void error(Throwable exception, Class<?> source, String identifier, String context, String info);

}
