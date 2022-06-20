package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.TestSummary
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
