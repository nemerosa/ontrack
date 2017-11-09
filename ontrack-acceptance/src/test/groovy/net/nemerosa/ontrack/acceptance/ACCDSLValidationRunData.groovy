package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCDSLValidationRunData extends AbstractACCDSL {

    @Test
    void 'Validation run with data'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setDataType(
                    "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType",
                    [
                            warningLevel: "HIGH",
                            warningValue: 10,
                            failedLevel : "CRITICAL",
                            failedValue : 1
                    ]
            )
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithData("VS", [
                CRITICAL: 1,
                HIGH    : 2,
                MEDIUM  : 4,
                LOW     : 8,
        ])
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType"
        assert run.data.data == [
                levels: [
                        CRITICAL: 1,
                        HIGH    : 2,
                        MEDIUM  : 4,
                        LOW     : 8,
                ]
        ]

    }

    @Test
    void 'Validation run with text'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setTextDataType()
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithText("VS", "PASSED", "Some text")
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.TextValidationDataType"
        assert run.data.data == "Some text"
    }

    @Test
    void 'Validation run with CHML'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setCHMLDataType(
                    "HIGH",
                    10,
                    "CRITICAL",
                    2
            )
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithCHML("VS", 1, 20, 4, 8)
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.status == "WARNING"
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType"
        assert run.data.data == [
                levels: [
                        CRITICAL: 1,
                        HIGH    : 20,
                        MEDIUM  : 4,
                        LOW     : 8,
                ]
        ]
    }

    @Test
    void 'Validation run with partial CHML'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setCHMLDataType(
                    "HIGH",
                    10,
                    "CRITICAL",
                    1
            )
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithCHML("VS", 1, 2)
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType"
        assert run.data.data == [
                levels: [
                        CRITICAL: 1,
                        HIGH    : 2,
                        MEDIUM  : 0,
                        LOW     : 0,
                ]
        ]
    }

    @Test
    void 'Validation run with number threshold'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setNumberDataType(
                    10,
                    20,
                    false
            )
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithNumber("VS", 15)
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.status == "WARNING"
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.ThresholdNumberValidationDataType"
        assert run.data.data == 15
    }

    @Test
    void 'Validation run with percentage threshold'() {
        def projectName = uid("P")
        // Validation stamp data type
        ontrack.project(projectName).branch("master") {
            validationStamp("VS").setPercentageDataType(
                    25,
                    10,
                    true
            )
        }
        // Creates a build and validates it
        def build = ontrack.branch(projectName, "master").build("1")
        build.validateWithPercentage("VS", 4)
        // Gets the run
        def run = build.validationRuns[0]
        // Gets the data
        assert run.status == "FAILED"
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType"
        assert run.data.data == 4
    }

}
