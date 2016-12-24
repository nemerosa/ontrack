package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter;
import net.nemerosa.ontrack.model.support.Page;

import java.util.List;

public interface ApplicationLogEntriesRepository {

    /**
     * Saves a log entry
     *
     * @param entry Entry to log
     */
    void log(ApplicationLogEntry entry);

    /**
     * Gets the total number of entries
     *
     * @return Number of entries
     */
    int getTotalCount();

    /**
     * List of messages
     *
     * @param filter Filter to use
     * @param page   Pagination instructions
     * @return List of entries for the filter and the page
     */
    List<ApplicationLogEntry> getLogEntries(ApplicationLogEntryFilter filter, Page page);

    /**
     * Removes all entries which are older than x days
     *
     * @param retentionDays Number of days to retain log entries
     */
    void cleanup(int retentionDays);

    /**
     * Deletes all log entries
     */
    void deleteLogEntries();
}
