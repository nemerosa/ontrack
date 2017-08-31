package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ValidationRunRequest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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

}