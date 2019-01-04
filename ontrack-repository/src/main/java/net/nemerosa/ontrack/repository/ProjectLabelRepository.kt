package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.labels.ProjectLabelForm

interface ProjectLabelRepository {

    /**
     * Gets the labels for a project
     */
    fun getLabelsForProject(project: Int): List<LabelRecord>

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

    /**
     * Sets all the labels for a project
     */
    fun associateProjectToLabels(project: Int, form: ProjectLabelForm)
}