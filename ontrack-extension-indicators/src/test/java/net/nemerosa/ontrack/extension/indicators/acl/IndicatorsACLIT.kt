package net.nemerosa.ontrack.extension.indicators.acl

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor.Companion.GLOBAL_INDICATOR_MANAGER
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor.Companion.GLOBAL_INDICATOR_PORTFOLIO_MANAGER
import net.nemerosa.ontrack.model.labels.ProjectLabelForm
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Testing the ACL for the indicator management.
 */
class IndicatorsACLIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Global indicator managers can assign projects to labels`() {
        `Test that role can assign labels to a project`(GLOBAL_INDICATOR_MANAGER)
    }

    @Test
    fun `Global indicator portfolio managers can assign projects to labels`() {
        `Test that role can assign labels to a project`(GLOBAL_INDICATOR_PORTFOLIO_MANAGER)
    }

    private fun `Test that role can assign labels to a project`(role: String) {
        withNoGrantViewToAll {
            val label = label()
            project {
                asAccountWithGlobalRole(role) {
                    projectLabelManagementService.associateProjectToLabels(this, ProjectLabelForm(listOf(label.id)))
                }
                // Checks the label has been assigned
                val assignedLabel = labels.find { it.id == label.id }
                assertNotNull(assignedLabel, "Label has been assigned")
            }
        }
    }

}