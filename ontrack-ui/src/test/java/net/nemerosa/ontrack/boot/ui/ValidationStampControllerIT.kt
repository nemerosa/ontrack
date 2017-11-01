package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ValidationStampInput
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValidationStampControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var validationStampController: ValidationStampController

    @Test
    fun validationStampWithDataType() {
        // Branch
        val branch = doCreateBranch()
        // Creates a validation stamp with an associated percentage data type
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            validationStampController.newValidationStamp(
                    branch.id,
                    ValidationStampInput(
                            "VSPercent",
                            "",
                            ServiceConfiguration(
                                    TestNumberValidationDataType::class.java.name,
                                    JsonUtils.format(mapOf("threshold" to 60))
                            )
                    )
            )
        }
        // Loads the validation stamp
        val loadedVs = validationStampController.getValidationStamp(vs.id)
        // Checks the data type is still there
        val dataType = loadedVs.dataType
        assertNotNull(dataType, "Data type is loaded")
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.id)
        assertEquals(60, dataType.config)
    }

}
