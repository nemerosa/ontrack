package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.ApplicationLogEntry;

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
}
