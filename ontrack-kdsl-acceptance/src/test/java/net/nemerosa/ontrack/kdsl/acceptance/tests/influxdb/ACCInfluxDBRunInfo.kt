package net.nemerosa.ontrack.kdsl.acceptance.tests.influxdb

import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import org.influxdb.InfluxDBFactory
import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import org.influxdb.dto.Query
import org.influxdb.impl.InfluxDBResultMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCInfluxDBRunInfo : AbstractACCDSLTestSupport() {

    @Test
    fun `Run info is exported to InfluxDB`() {
        val project = project {
            branch("1.0") {
                build("1.0.0", runTime = 20) {}
            }
            this
        }

        // TODO Checks the time has been written in InfluxDB

        val influxDB = InfluxDBFactory.connect(
            ACCProperties.InfluxDB.url
        )

        val result = influxDB.query(
            Query(
                """
                    SELECT *
                    FROM ontrack_acceptance_run_build_time_seconds
                    WHERE project = '${project.name}'
                    AND branch = '1.0'
                """.trimIndent(),
                "ontrack"
            )
        )

        val resultMapper = InfluxDBResultMapper()
        val measurements: List<RunInfoMeasurement> = resultMapper.toPOJO(
            result,
            RunInfoMeasurement::class.java
        )

        val measurement = measurements.firstOrNull()
        assertNotNull(measurement, "One measurement found") {
            assertEquals(project.name, it.project)
            assertEquals("1.0", it.branch)
            assertEquals(20, it.value)
        }

    }

    @Measurement(name = "ontrack_acceptance_run_build_time_seconds")
    class RunInfoMeasurement {

        @field:Column(name = "value")
        var value: Int = -1

        @field:Column(name = "project")
        var project: String = ""

        @field:Column(name = "branch")
        var branch: String = ""
    }
}