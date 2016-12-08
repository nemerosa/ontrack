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
     *
     * @return Count of entries
     */
    int getLogEntriesTotal();

    /**
     * List of messages
     *
     * @param filter Filter to use
     * @param page   Pagination instructions
     * @return List of entries for the filter and the page
     */
    List<ApplicationLogEntry> getLogEntries(ApplicationLogEntryFilter filter, Page page);

    /**
     * Logs an entry
     *
     * @param entry Entry to log
     */
    void log(ApplicationLogEntry entry);

    /**
     * Removes all entries which are older than x days
     *
     * @param retentionDays Number of days to retain log entries
     */
    void cleanup(int retentionDays);
}
