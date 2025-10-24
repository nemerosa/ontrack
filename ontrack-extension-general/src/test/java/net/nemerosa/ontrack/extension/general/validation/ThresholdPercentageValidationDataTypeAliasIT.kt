package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.EnvFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.fail

class ThresholdPercentageValidationDataTypeAliasIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Test
    @AsAdminTest
    fun `CHML validation data type alias`() {
        val branch = configTestSupport.configureBranch(
            yaml = """
                version: v1
                configuration:
                    defaults:
                        branch:
                            validations:
                                PERCENTAGE:
                                    percentage:
                                        okIfGreater: false
                                        warningThreshold: 50
                                        failureThreshold: 80
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic()
        )

        val vs = structureService.findValidationStampByName(branch.project.name, branch.name, "PERCENTAGE").getOrNull()
            ?: fail("Cannot find PERCENTAGE validation stamp")

        assertEquals(ThresholdPercentageValidationDataType::class.qualifiedName, vs.dataType?.descriptor?.id)
        assertIs<ThresholdConfig>(vs.dataType?.config) {
            assertEquals(50, it.warningThreshold)
            assertEquals(80, it.failureThreshold)
            assertEquals(false, it.okIfGreater)
        }
    }

}