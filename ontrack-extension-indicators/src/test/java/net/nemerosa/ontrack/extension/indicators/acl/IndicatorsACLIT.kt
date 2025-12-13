package net.nemerosa.ontrack.extension.indicators.acl

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorRoleContributor.Companion.GLOBAL_INDICATOR_MANAGER
import net.nemerosa.ontrack.model.labels.ProjectLabelForm
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

/**
 * Testing the ACL for the indicator management.
 */
class IndicatorsACLIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Global indicator managers can assign projects to labels`() {
        withNoGrantViewToAll {
            val label = label()
            this.project {
                asAccountWithGlobalRole(GLOBAL_INDICATOR_MANAGER) {
                    projectLabelManagementService.associateProjectToLabels(this, ProjectLabelForm(listOf(label.id)))
                }
                // Checks the label has been assigned
                val assignedLabel = labels.find { it.id == label.id }
                assertNotNull(assignedLabel, "Label has been assigned")
            }
        }
    }

}