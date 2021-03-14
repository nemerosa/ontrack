package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.config
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestSummaryValidationDataTypeRunGraphQLMutationIT :
    AbstractValidationDataTypeRunGraphQLMutationTestSupport<TestSummaryValidationConfig>() {

    @Autowired
    private lateinit var testSummaryValidationDataType: TestSummaryValidationDataType

    @Test
    fun `Passed validation tests`() {
        testValidation(expectedStatus = "PASSED")
    }

    @Test
    fun `Warning validation tests`() {
        testValidation(skipped = 1, expectedStatus = "WARNING")
    }

    @Test
    fun `Failed validation tests`() {
        testValidation(failed = 1, expectedStatus = "FAILED")
    }

    private fun testValidation(
        skipped: Int = 0,
        failed: Int = 0,
        expectedStatus: String,
    ) {
        val dataConfig = testSummaryValidationDataType.config(
            TestSummaryValidationConfig(
                warningIfSkipped = true
            )
        )
        val dataInput = """
            passed: 10,
            skipped: $skipped,
            failed: $failed
        """.trimIndent()
        val expectedData = mapOf(
            "passed" to 10,
            "skipped" to skipped,
            "failed" to failed,
            "total" to (10 + skipped + failed)
        ).asJson()
        //
        testValidationByName(
            dataConfig = dataConfig,
            mutationName = "validateBuildWithTests",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = expectedStatus
        )
        //
        testValidationById(
            dataConfig = dataConfig,
            mutationName = "validateBuildByIdWithTests",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = expectedStatus
        )
    }

}