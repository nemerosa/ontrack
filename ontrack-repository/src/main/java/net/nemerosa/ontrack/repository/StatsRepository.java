package net.nemerosa.ontrack.repository;

/**
 * Collection of statistics about the data.
 */
public interface StatsRepository {
    int getProjectCount();

    int getBranchCount();

    int getBuildCount();

    int getPromotionLevelCount();

    int getPromotionRunCount();

    int getValidationStampCount();

    int getValidationRunCount();

    int getValidationRunStatusCount();

    int getPropertyCount();

    int getEventCount();
}
