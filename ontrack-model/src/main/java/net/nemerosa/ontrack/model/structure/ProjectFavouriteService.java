package net.nemerosa.ontrack.model.structure;

public interface ProjectFavouriteService {

    /**
     * Is this project a favourite?
     */
    boolean isProjectFavourite(Project project);

    /**
     * Sets a project as favourite (or not)
     */
    void setProjectFavourite(Project project, boolean favourite);

}
