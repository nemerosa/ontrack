package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.influxdb.InfluxDBFactory
import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import org.influxdb.dto.Query
import org.influxdb.impl.InfluxDBResultMapper
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCInfluxDBRunInfo extends AcceptanceTestClient {

    @Test
    void 'Run info is exported to InfluxDB'() {
        def p = uid("P")
        ontrack.project(p) {
            branch("1.0") {
                build("1.0.0") {
                    runInfo = [
                            runTime: 120
                    ]
                }
            }
        }
        // Checks the time has been written in InfluxDB
        def influxDB = InfluxDBFactory.connect(
                configRule.config.influxdbUri
        )
        def result = influxDB.query(
                new Query(
                        "SELECT * " +
                                "FROM ontrack_value_run_info_build_time_seconds " +
                                "WHERE project = '${p}' " +
                                "AND branch = '1.0'",
                        "ontrack"
                )
        )
        def resultMapper = new InfluxDBResultMapper()
        def measurements = resultMapper.toPOJO(
                result,
                RunInfoMeasurement
        )
        assert !measurements.empty
        def measurement = measurements.first()
        assert measurement.value == 120
        assert measurement.project == p
        assert measurement.branch == "1.0"
        assert measurement.name == "1.0.0"
    }

    @Measurement(name = "ontrack_value_run_info_build_time_seconds")
    static class RunInfoMeasurement {
        @Column(name = "value")
        int value
        @Column(name = "project")
        String project
        @Column(name = "branch")
        String branch
        @Column(name = "name")
        String name
    }

}
