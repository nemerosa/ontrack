package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.support.ApplicationLogEntry;

public interface ApplicationLogEntriesRepository {

    /**
     * Saves a log entry
     */
    void log(ApplicationLogEntry entry);

}
