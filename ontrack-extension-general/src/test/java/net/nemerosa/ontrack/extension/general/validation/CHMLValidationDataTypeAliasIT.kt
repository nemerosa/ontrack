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

class CHMLValidationDataTypeAliasIT : AbstractDSLTestSupport() {

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
                                CHML:
                                    chml:
                                        warningLevel: HIGH
                                        warningValue: 1
                                        failedLevel: CRITICAL
                                        failedValue: 1
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = EnvFixtures.generic()
        )

        val vs = structureService.findValidationStampByName(branch.project.name, branch.name, "CHML").getOrNull()
            ?: fail("Cannot find CHML validation stamp")

        assertEquals(CHMLValidationDataType::class.qualifiedName, vs.dataType?.descriptor?.id)
        assertIs<CHMLValidationDataTypeConfig>(vs.dataType?.config) {
            assertEquals(CHMLLevel(CHML.HIGH, 1), it.warningLevel)
            assertEquals(CHMLLevel(CHML.CRITICAL, 1), it.failedLevel)
        }
    }

}