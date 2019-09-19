package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.TestSummary
import org.influxdb.InfluxDBFactory
import org.influxdb.annotation.Column
import org.influxdb.annotation.Measurement
import org.influxdb.dto.Query
import org.influxdb.impl.InfluxDBResultMapper
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Checks that validation run data is sent to InfluxDB
 */
@AcceptanceTestSuite
class ACCInfluxDBValidationData extends AcceptanceTestClient {

    @Test
    void 'Validation run data is exported to InfluxDB'() {
        def p = uid("P")
        ontrack.project(p) {
            branch("master") {
                validationStamp("VS")
                build("1.0.0") {
                    validateWithTestSummary("VS", new TestSummary(
                            passed: 10, skipped: 1, failed: 0
                    ), "PASSED")
                }
            }
        }
        // Checks the data has been written in InfluxDB
        def influxDB = InfluxDBFactory.connect(
                configRule.config.influxdbUri
        )
        def result = influxDB.query(
                new Query(
                        "SELECT * " +
                                "FROM ontrack_value_validation_data " +
                                "WHERE project = '${p}' " +
                                "AND branch = 'master' " +
                                "AND validation = 'VS'",
                        "ontrack"
                )
        )
        def resultMapper = new InfluxDBResultMapper()
        def measurements = resultMapper.toPOJO(
                result,
                ValidationDataMeasurement
        )
        assert !measurements.empty
        def measurement = measurements.first()
        assert measurement.project == p
        assert measurement.branch == "master"
        assert measurement.build == "1.0.0"
        assert measurement.validation == "VS"
        assert measurement.status == "PASSED"
        assert measurement.type == "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType"
        assert measurement.passed == 10
        assert measurement.skipped == 1
        assert measurement.failed == 0
        assert measurement.total == 11
    }

    @Test
    void 'Metrics validation run data is exported to InfluxDB'() {
        def p = uid("P")
        ontrack.project(p) {
            branch("master") {
                validationStamp("VS")
                build("1.0.0") {
                    validateWithMetrics("VS", [
                            "js.bundle": 1500.56,
                            "js.error" : 150
                    ], "PASSED")
                }
            }
        }
        // Checks the data has been written in InfluxDB
        def influxDB = InfluxDBFactory.connect(
                configRule.config.influxdbUri
        )
        def result = influxDB.query(
                new Query(
                        "SELECT * " +
                                "FROM ontrack_value_validation_data " +
                                "WHERE project = '${p}' " +
                                "AND branch = 'master' " +
                                "AND validation = 'VS'",
                        "ontrack"
                )
        )
        def resultMapper = new InfluxDBResultMapper()
        def measurements = resultMapper.toPOJO(
                result,
                MetricsValidationDataMeasurement
        )
        assert !measurements.empty
        def measurement = measurements.first()
        assert measurement.project == p
        assert measurement.branch == "master"
        assert measurement.build == "1.0.0"
        assert measurement.validation == "VS"
        assert measurement.status == "PASSED"
        assert measurement.type == "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType"
        assert measurement.bundle == 1500.56d
        assert measurement.error == 150d
    }

    @Measurement(name = "ontrack_value_validation_data")
    static class ValidationDataMeasurement {
        @Column(name = "project")
        String project
        @Column(name = "branch")
        String branch
        @Column(name = "build")
        String build
        @Column(name = "validation")
        String validation
        @Column(name = "status")
        String status
        @Column(name = "type")
        String type
        @Column(name = "passed")
        int passed
        @Column(name = "skipped")
        int skipped
        @Column(name = "failed")
        int failed
        @Column(name = "total")
        int total
    }

    @Measurement(name = "ontrack_value_validation_data")
    static class MetricsValidationDataMeasurement {
        @Column(name = "project")
        String project
        @Column(name = "branch")
        String branch
        @Column(name = "build")
        String build
        @Column(name = "validation")
        String validation
        @Column(name = "status")
        String status
        @Column(name = "type")
        String type
        @Column(name = "js.bundle")
        double bundle
        @Column(name = "js.error")
        double error
    }

}
