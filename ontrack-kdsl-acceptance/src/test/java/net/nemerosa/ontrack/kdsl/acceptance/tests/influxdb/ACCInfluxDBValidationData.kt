package net.nemerosa.ontrack.kdsl.acceptance.tests.influxdb

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.extension.general.TestSummary
import net.nemerosa.ontrack.kdsl.spec.extension.general.validateWithTestSummary
import org.influxdb.InfluxDBFactory
import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import org.influxdb.dto.Query
import org.influxdb.impl.InfluxDBResultMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AcceptanceTestSuite
class ACCInfluxDBValidationData : AbstractACCDSLTestSupport() {

    @Test
    fun `Validation run data is exported to InfluxDB`() {
        val project = project {
            branch("main") {
                val vs = validationStamp("VS")
                build("1.0.0") {
                    validateWithTestSummary(
                        validation = vs.name,
                        status = "PASSED",
                        testSummary = TestSummary(
                            passed = 10,
                            skipped = 1,
                            failed = 0,
                        )
                    )
                }
            }
            this
        }
        // Checks the data has been written in InfluxDB
        val influxDB = InfluxDBFactory.connect(
            ACCProperties.InfluxDB.url
        )
        val result = influxDB.query(
            Query(
                """
                    SELECT * 
                    FROM ontrack_acceptance_validation_data 
                    WHERE project = '${project.name}' 
                    AND branch = 'main' 
                    AND validation = 'VS'
                """.trimIndent(),
                "ontrack"
            )
        )

        val resultMapper = InfluxDBResultMapper()
        val measurements: List<TestSummaryValidationData> = resultMapper.toPOJO(
            result,
            TestSummaryValidationData::class.java
        )

        val measurement = measurements.firstOrNull()
        assertNotNull(measurement, "One measurement found") {
            assertEquals(project.name, it.project)
            assertEquals("main", it.branch)
            assertEquals("VS", it.validation)
            assertEquals("PASSED", it.status)
            assertEquals("net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType", it.type)
            assertEquals(10, it.passed)
            assertEquals(1, it.skipped)
            assertEquals(0, it.failed)
            assertEquals(11, it.total)
        }

    }
}

@Measurement(name = "ontrack_acceptance_validation_data")
class TestSummaryValidationData {

    @field:Column(name = "project")
    var project: String = ""

    @field:Column(name = "branch")
    var branch: String = ""

    @field:Column(name = "validation")
    var validation: String = ""

    @field:Column(name = "status")
    var status: String = ""

    @field:Column(name = "type")
    var type: String = ""

    @field:Column(name = "passed")
    var passed: Int = -1

    @field:Column(name = "skipped")
    var skipped: Int = -1

    @field:Column(name = "failed")
    var failed: Int = -1

    @field:Column(name = "total")
    var total: Int = -1
}
