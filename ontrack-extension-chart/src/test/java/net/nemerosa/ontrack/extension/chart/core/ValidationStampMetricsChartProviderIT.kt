package net.nemerosa.ontrack.extension.chart.core

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.chart.GetChartOptions
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationData
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValidationStampMetricsChartProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var provider: ValidationStampMetricsChartProvider

    @Autowired
    private lateinit var metricsValidationDataType: MetricsValidationDataType

    @Test
    fun `Chart metrics based on untyped validation stamps and partially filled validation runs`() {
        project {
            branch {
                val ref = Time.now()
                val vs = validationStamp()
                // Creating some builds, some with validations, some without validation,
                // some with validations, but no metrics
                build {
                    updateBuildSignature(time = ref.minusDays(5))
                }
                build {
                    updateBuildSignature(time = ref.minusDays(4))
                    validate(vs, signature = Signature.of(ref.minusDays(4), "test"))
                }
                build {
                    updateBuildSignature(time = ref.minusDays(3))
                }
                build {
                    updateBuildSignature(time = ref.minusDays(2))
                    validateWithData(
                        validationStamp = vs,
                        validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                        validationDataTypeId = metricsValidationDataType::class.java.name,
                        validationRunData = MetricsValidationData(
                            metrics = mapOf(
                                "blocking_issues" to 4.0,
                                "coverage" to 56.0,
                            )
                        ),
                        signature = Signature.of(ref.minusDays(2), "test")
                    )
                }
                build {
                    updateBuildSignature(time = ref.minusDays(1))
                    validateWithData(
                        validationStamp = vs,
                        validationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
                        validationDataTypeId = metricsValidationDataType::class.java.name,
                        validationRunData = MetricsValidationData(
                            metrics = mapOf(
                                "blocking_issues" to 2.0,
                                "coverage" to 66.0,
                            )
                        ),
                        signature = Signature.of(ref.minusDays(1), "test")
                    )
                }

                // Getting the charts for the metrics of this validation stamp
                val def = provider.getChartDefinition(vs)
                assertNotNull(def, "Chart definition is available") {
                    assertEquals("Validation stamp metrics", it.title)
                }

                // Get the chart values
                val chart = provider.getChart(
                    GetChartOptions(
                        ref = ref,
                        interval = "1w",
                        period = "1d",
                    ),
                    ValidationStampChartParameters(vs.id())
                )

                // Checking the chart values
                assertEquals(
                    setOf(
                        "blocking_issues",
                        "coverage"
                    ),
                    chart.metricNames.toSet()
                )
                assertEquals(
                    listOf(
                        emptyMap(),
                        emptyMap(),
                        emptyMap(),
                        emptyMap(),
                        mapOf(
                            "blocking_issues" to 4.0,
                            "coverage" to 56.0,
                        ),
                        mapOf(
                            "blocking_issues" to 2.0,
                            "coverage" to 66.0,
                        ),
                        emptyMap(),
                    ),
                    chart.metricValues
                )
            }
        }
    }

}