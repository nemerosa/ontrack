package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCDSLValidationStampDataType extends AbstractACCDSL {

    @Test
    void 'Validation stamp with data type'() {
        def projectName = uid("P")
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
        // Gets the validation stamp
        def vs = ontrack.validationStamp(projectName, "master", "VS")
        assert vs != null
        // Gets the data type
        def type = vs.dataType
        assert type != null
        assert type.id == "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType"
        assert type.config == [
                warningLevel: [
                        level: "HIGH",
                        value: 10,
                ],
                failedLevel : [
                        level: "CRITICAL",
                        value: 1,
                ],
        ]

    }

}
