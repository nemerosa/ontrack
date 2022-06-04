package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AutoVersioningConfigTest {

    @Test
    fun `Checking duplicates OK`() {
        AutoVersioningConfig(
            listOf(
                config(sourceProject = "P1"),
                config(sourceProject = "P2")
            )
        ).validate()
    }

    @Test
    fun `Checking duplicates NOT OK`() {
        val ex = assertFailsWith<AutoVersioningConfigDuplicateProjectException> {
            AutoVersioningConfig(
                listOf(
                    config(sourceProject = "P1"),
                    config(sourceProject = "P1"),
                    config(sourceProject = "P2"),
                    config(sourceProject = "P3"),
                    config(sourceProject = "P3")
                )
            ).validate()
        }
        assertEquals(
            "It is not possible to configure a source project multiple times. Duplicate projects are: P1, P3",
            ex.message
        )
    }

    private fun config(
        sourceProject: String = uid("P"),
    ) = AutoVersioningSourceConfig(
        sourceProject = sourceProject,
        sourceBranch = "master",
        sourcePromotion = "IRON",
        targetPath = "gradle.properties",
        targetProperty = "myVersion",
        targetRegex = null,
        targetPropertyRegex = null,
        targetPropertyType = null,
        autoApproval = null,
        upgradeBranchPattern = null,
        postProcessing = null,
        postProcessingConfig = null,
        validationStamp = null,
        autoApprovalMode = AutoApprovalMode.SCM
    )

}
