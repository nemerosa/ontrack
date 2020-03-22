package net.nemerosa.ontrack.model.structure

interface ProjectFavouriteService {

    /**
     * Gets the favourite projects for the current user
     */
    fun getFavouriteProjects(): List<Project>

    /**
     * Is this project a favourite?
     */
    fun isProjectFavourite(project: Project): Boolean

    /**
     * Sets a project as favourite (or not)
     */
    fun setProjectFavourite(project: Project, favourite: Boolean)
}