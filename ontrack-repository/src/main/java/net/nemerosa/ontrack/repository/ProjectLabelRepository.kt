package net.nemerosa.ontrack.repository

interface ProjectLabelRepository {

    /**
     * Gets the labels for a project
     */
    fun getLabelsForProject(project: Int): List<Int>

    /**
     * Gets the projects for a label
     *
     * @return List of project IDs
     */
    fun getProjectsForLabel(label: Int): List<Int>

    /**
     * Associates a label to a project
     */
    fun associateProjectToLabel(project: Int, label: Int)

    /**
     * Removes the association between a label and a project
     */
    fun unassociateProjectToLabel(project: Int, label: Int)
}