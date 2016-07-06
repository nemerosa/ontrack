package net.nemerosa.ontrack.repository;

public interface ProjectFavouriteRepository {

    /**
     * Is this project a favourite?
     */
    boolean isProjectFavourite(int accountId, int projectId);

    /**
     * Sets a project as favourite (or not)
     */
    void setProjectFavourite(int accountId, int projectId, boolean favourite);

}
