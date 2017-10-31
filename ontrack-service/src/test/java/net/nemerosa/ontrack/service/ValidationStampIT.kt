package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.security.ValidationStampEdit
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.model.structure.validationDataTypeConfig
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
                            TestNumberValidationDataType::class.validationDataTypeConfig(2)
                    )
            )
        }
        // Loads the validation stamp
        var loadedVs = asUserWithView(branch).call { structureService.getValidationStamp(vs.id) }
        // Checks the data type is still there
        @Suppress("UNCHECKED_CAST")
        var dataType: ValidationDataTypeConfig<Int?> = loadedVs.dataType as ValidationDataTypeConfig<Int?>
        assertNotNull("Data type is loaded", dataType)
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.id)
        assertEquals(2, dataType.config)
        // Loads using the list
        val vsList = asUserWithView(branch).call { structureService.getValidationStampListForBranch(branch.id) }
        assertEquals(1, vsList.size)
        assertEquals(loadedVs.id, vsList.first().id)
        // Updates it (with a threshold)
        asUser().with(branch, ValidationStampEdit::class.java).execute {
            structureService.saveValidationStamp(
                    loadedVs.withDataType(
                            TestNumberValidationDataType::class.validationDataTypeConfig(60)
                    )
            )
        }
        // Reloads it and check
        loadedVs = asUserWithView(branch).call { structureService.getValidationStamp(vs.id) }
        // Checks the data type is still there
        @Suppress("UNCHECKED_CAST")
        dataType = loadedVs.dataType as ValidationDataTypeConfig<Int?>
        assertNotNull("Data type is loaded", dataType)
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.id)
        assertEquals(60, dataType.config)
    }

}