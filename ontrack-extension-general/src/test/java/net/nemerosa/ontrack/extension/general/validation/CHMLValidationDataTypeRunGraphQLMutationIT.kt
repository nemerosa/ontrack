package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.config
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CHMLValidationDataTypeRunGraphQLMutationIT :
    AbstractValidationDataTypeRunGraphQLMutationTestSupport<CHMLValidationDataTypeConfig>() {

    @Autowired
    private lateinit var chmlValidationDataType: CHMLValidationDataType

    @Test
    fun `Passed validation CHML by name`() {
        testValidation(medium = 1, expectedStatus = "PASSED")
    }

    @Test
    fun `Warning validation CHML`() {
        testValidation(high = 1, expectedStatus = "WARNING")
    }

    @Test
    fun `Failed validation CHML`() {
        testValidation(critical = 1, expectedStatus = "FAILED")
    }

    private fun testValidation(
        critical: Int = 0,
        high: Int = 0,
        medium: Int = 0,
        expectedStatus: String,
    ) {
        val dataConfig = chmlValidationDataType.config(
            CHMLValidationDataTypeConfig(
                warningLevel = CHMLLevel(CHML.HIGH, 1),
                failedLevel = CHMLLevel(CHML.CRITICAL, 1),
            )
        )
        val dataInput = """
                critical: $critical,
                high: $high,
                medium: $medium
            """.trimIndent()
        val expectedData = mapOf(
            "levels" to
                    mapOf(
                        "CRITICAL" to critical,
                        "HIGH" to high,
                        "MEDIUM" to medium,
                        "LOW" to 0,
                    )
        ).asJson()
        // Run info
        val runInfo = RunInfoInput(
            sourceType = "github",
            sourceUri = "URL to GitHub",
            triggerType = "push",
            runTime = 24
        )
        //
        testValidationByName(
            dataConfig = dataConfig,
            mutationName = "validateBuildWithCHML",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = expectedStatus,
            runInfo = runInfo,
        )
        //
        testValidationById(
            dataConfig = dataConfig,
            mutationName = "validateBuildByIdWithCHML",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = expectedStatus,
            runInfo = runInfo,
        )
    }

}
