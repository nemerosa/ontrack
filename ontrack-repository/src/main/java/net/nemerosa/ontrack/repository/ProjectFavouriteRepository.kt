package net.nemerosa.ontrack.repository

interface ProjectFavouriteRepository {

    /**
     * Gets the list of favourite projects for a user
     *
     * @param accountId ID of the user
     * @return List of project IDs
     */
    fun getFavouriteProjects(accountId: Int): List<Int>

    /**
     * Is this project a favourite?
     */
    fun isProjectFavourite(accountId: Int, projectId: Int): Boolean

    /**
     * Sets a project as favourite (or not)
     */
    fun setProjectFavourite(accountId: Int, projectId: Int, favourite: Boolean)
}