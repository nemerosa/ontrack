package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.labels.ProjectLabelManagement
import org.junit.Test
import kotlin.test.assertTrue

class ProjectLabelManagementServiceIT : AbstractDSLTestSupport() {

    @Test
    fun associateProjectToLabel() {
        val label = label()
        project {
            // Association
            asUser().with(this, ProjectLabelManagement::class.java).execute {
                projectLabelManagementService.associateProjectToLabel(this, label)
            }
            // Testing the association
            asUserWithView(this).execute {
                val projects = projectLabelManagementService.getProjectsForLabel(label)
                assertTrue(projects.contains(id), "Project is associated to label")
                assertTrue(
                        projectLabelManagementService.getLabelsForProject(this)
                                .map { it.id }
                                .contains(label.id),
                        "Label is associated to project"
                )
            }
            // Removing the association
            asUser().with(this, ProjectLabelManagement::class.java).execute {
                projectLabelManagementService.unassociateProjectToLabel(this, label)
            }
            // Testing the association
            asUserWithView(this).execute {
                val projects = projectLabelManagementService.getProjectsForLabel(label)
                assertTrue(projects.isEmpty(), "Project is not associated to label")
                assertTrue(
                        projectLabelManagementService.getLabelsForProject(this).isEmpty(),
                        "Label is not associated to project"
                )
            }
        }
    }
}