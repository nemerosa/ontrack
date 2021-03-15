package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.config
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MetricsValidationDataTypeRunGraphQLMutationIT :
    AbstractValidationDataTypeRunGraphQLMutationTestSupport<Any?>() {

    @Autowired
    private lateinit var metricsValidationDataType: MetricsValidationDataType

    @Test
    fun `Passing validation`() {
        val dataConfig = metricsValidationDataType.config(null)
        val dataInput = """
            metrics: [
                {
                    name: "lead-time",
                    value: 4.5
                },
                {
                    name: "mttr",
                    value: 18.1
                }
            ]
        """.trimIndent()
        val expectedData = mapOf(
            "metrics" to mapOf(
                "lead-time" to 4.5,
                "mttr" to 18.1
            )
        ).asJson()
        //
        testValidationByName(
            dataConfig = dataConfig,
            mutationName = "validateBuildWithMetrics",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = "PASSED"
        )
        //
        testValidationById(
            dataConfig = dataConfig,
            mutationName = "validateBuildByIdWithMetrics",
            dataInput = dataInput,
            expectedData = expectedData,
            expectedStatus = "PASSED"
        )
    }

}