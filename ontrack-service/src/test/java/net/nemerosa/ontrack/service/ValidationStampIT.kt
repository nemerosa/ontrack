package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.security.ValidationStampEdit
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ValidationStampIT : AbstractServiceTestSupport() {

    @Test
    fun validationStampWithDataType() {
        // Branch
        val branch = doCreateBranch()
        // Creates a validation stamp with an associated percentage data type
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            NameDescription.nd("VSPercent", "")
                    ).withDataType(
                            ServiceConfiguration(
                                    TestNumberValidationDataType::class.java.name,
                                    JsonUtils.format(2)
                            )
                    )
            )
        }
        // Loads the validation stamp
        var loadedVs = asUserWithView(branch).call { structureService.getValidationStamp(vs.id) }
        // Checks the data type is still there
        var dataType = loadedVs.dataType
        assertNotNull("Data type is loaded", dataType)
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.id)
        TestUtils.assertJsonEquals(
                JsonUtils.format(2),
                dataType.data
        )
        // Loads using the list
        val vsList = asUserWithView(branch).call { structureService.getValidationStampListForBranch(branch.id) }
        assertEquals(1, vsList.size)
        assertEquals(loadedVs, vsList.first())
        // Updates it (with a threshold)
        asUser().with(branch, ValidationStampEdit::class.java).execute {
            structureService.saveValidationStamp(
                    loadedVs.withDataType(
                            ServiceConfiguration(
                                    TestNumberValidationDataType::class.java.name,
                                    JsonUtils.format(60)
                            )
                    )
            )
        }
        // Reloads it and check
        loadedVs = asUserWithView(branch).call { structureService.getValidationStamp(vs.id) }
        // Checks the data type is still there
        dataType = loadedVs.dataType
        assertNotNull("Data type is loaded", dataType)
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.id)
        TestUtils.assertJsonEquals(
                JsonUtils.format(60),
                dataType.data
        )
    }

}