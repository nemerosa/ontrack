package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project

interface ProjectLabelManagementService {

    /**
     * Gets the labels for a project
     */
    fun getLabelsForProject(project: Project): List<Label>

    /**
     * Gets the projects for a label
     *
     * @return List of project IDs
     */
    fun getProjectsForLabel(label: Label): List<ID>

    /**
     * Associates a label to a project
     */
    fun associateProjectToLabel(project: Project, label: Label)

    /**
     * Removes the association between a label and a project
     */
    fun unassociateProjectToLabel(project: Project, label: Label)

}