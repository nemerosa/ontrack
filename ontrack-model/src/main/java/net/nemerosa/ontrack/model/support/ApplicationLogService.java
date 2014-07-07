package net.nemerosa.ontrack.model.support;

import java.util.Map;

/**
 * This service is used to log messages at application level, to be seen by operation and administration people.
 * <p>
 * Having a message here would mean that something is defective in the application or in its configuration.
 */
public interface ApplicationLogService {

    /**
     * Logs an error.
     *
     * @param exception     Exception associated with this error, can be null
     * @param service       Source of the error
     * @param info          Properties for the error source
     * @param message       Descriptive message for the error
     * @param messageParams Parameters for the error message
     */
    void error(Throwable exception, String service, Map<String, ?> info, String message, Object... messageParams);

}
