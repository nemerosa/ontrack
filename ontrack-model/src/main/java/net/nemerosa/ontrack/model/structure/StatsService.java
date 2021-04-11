package net.nemerosa.ontrack.model.structure;

/**
 * Collection of statistics about the data.
 */
public interface StatsService {
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
