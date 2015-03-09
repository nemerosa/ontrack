package net.nemerosa.ontrack.repository;

/**
 * Collection of statistics about the data.
 */
public interface StatsRepository {
    int getProjectCount();
    int getBranchCount();
}
