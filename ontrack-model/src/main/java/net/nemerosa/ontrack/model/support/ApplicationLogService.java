package net.nemerosa.ontrack.model.support;

import java.util.List;

/**
 * This service is used to log messages at application level, to be seen by operation and administration people.
 * <p>
 * Having a message here would mean that something is defective in the application or in its configuration.
 */
public interface ApplicationLogService {

    /**
     * Total list of messages
     */
    int getLogEntriesTotal();

    /**
     * List of messages
     */
    List<ApplicationLogEntry> getLogEntries(Page page);

    /**
     * Logs an entry
     */
    void log(ApplicationLogEntry entry);
}
