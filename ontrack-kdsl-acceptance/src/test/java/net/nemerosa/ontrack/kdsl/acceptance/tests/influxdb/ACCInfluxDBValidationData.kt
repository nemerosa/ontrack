package net.nemerosa.ontrack.kdsl.acceptance.tests.influxdb

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.extension.general.TestSummary
import net.nemerosa.ontrack.kdsl.spec.extension.general.validateWithTestSummary
import org.junit.jupiter.api.Test

@AcceptanceTestSuite
class ACCInfluxDBValidationData : AbstractACCDSLTestSupport() {

    @Test
    fun `Validation run data is exported to InfluxDB`() {
        project {
            branch {
                val vs = validationStamp()
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
        }
//        // Checks the data has been written in InfluxDB
//        def influxDB = InfluxDBFactory.connect(
//                configRule.config.influxdbUri
//                )
//        def result = influxDB.query(
//                new Query(
//                        "SELECT * " +
//                                "FROM ontrack_value_validation_data " +
//                                "WHERE project = '${p}' " +
//                                "AND branch = 'master' " +
//                                "AND validation = 'VS'",
//        "ontrack"
//        )
//        )
//        def resultMapper = new InfluxDBResultMapper()
//        def measurements = resultMapper.toPOJO(
//                result,
//        ValidationDataMeasurement
//        )
//        assert !measurements.empty
//        def measurement = measurements.first()
//        assert measurement.project == p
//                assert measurement.branch == "master"
//        assert measurement.build == "1.0.0"
//        assert measurement.validation == "VS"
//        assert measurement.status == "PASSED"
//        assert measurement.type == "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType"
//        assert measurement.passed == 10
//        assert measurement.skipped == 1
//        assert measurement.failed == 0
//        assert meas
//        def p = urement.total == 11
    }
}