package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationRunControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var validationRunController: ValidationRunController

    @Test
    fun `Validation with no required data`() {
        // Creates a validation stamp with no data type
        val vs = doCreateValidationStamp()
        // Creates a build
        val build = doCreateBuild(vs.branch, NameDescription.nd("1", ""))
        // Validates the build
        val run = asUser().with(vs, ValidationRunCreate::class.java).call {
            validationRunController.newValidationRun(
                    build.id,
                    ValidationRunRequest(
                            null,
                            ServiceConfiguration(vs.name, null),
                            null,
                            "PASSED",
                            "No description"
                    )
            )
        }
        // Checks the run has no data
        assertNull(run.data, "Validation run has no data")
    }

    @Test
    fun `Validation with required data and status being passed`() {
        // Creates a validation stamp with a percentage type
        val branch = doCreateBranch()
        val vs = asUser().with(branch.getProject().id(), ValidationStampCreate::class.java).call({
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VS1", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    ThresholdPercentageValidationDataType::class.java.name,
                                    JsonUtils.format(
                                            ThresholdPercentageValidationDataTypeConfig(60)
                                    )
                            )
                    )
            )
        }
        )
        // Creates a build
        val build = doCreateBuild(branch, NameDescription.nd("1", ""))
        // Validates the build with some data, and a fixed status
        val run = asUser().with(vs, ValidationRunCreate::class.java).call {
            validationRunController.newValidationRun(
                    build.id,
                    ValidationRunRequest(
                            null,
                            ServiceConfiguration(
                                    vs.name,
                                    JsonUtils.`object`()
                                            .with("data", JsonUtils.`object`()
                                                    .with("value", 70)
                                                    .end())
                                            .end()
                            ),
                            null,
                            "FAILED",
                            "No description"
                    )
            )
        }
        // Checks the run has some data
        val data = run.data
        assertNotNull(data, "Validation run has some data")
        assertEquals(ThresholdPercentageValidationDataType::class.qualifiedName, data.id)
        TestUtils.assertJsonEquals(
                JsonUtils.`object`()
                        .with("value", 70)
                        .end(),
                data.data
        )
    }

}