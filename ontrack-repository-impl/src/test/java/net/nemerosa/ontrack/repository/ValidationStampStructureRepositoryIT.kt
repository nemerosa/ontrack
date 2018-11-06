package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.it.NOPValidationDataType
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.model.structure.config
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationStampStructureRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var nopValidationDataType: NOPValidationDataType

    @Test
    fun `Validation stamp with data type`() {
        val branch = do_create_branch()
        // Creates a validation stamp with a data type
        val vsId = structureRepository.newValidationStamp(
                ValidationStamp.of(
                        branch,
                        NameDescription.nd("VS", "")
                ).withDataType(
                        nopValidationDataType.config(60)
                )
        ).id
        // Loads the validation stamp
        val vs = structureRepository.getValidationStamp(vsId)
        assertEquals("VS", vs.name)
        assertNotNull(vs.dataType, "Data type not null").apply {
            assertEquals(NOPValidationDataType::class.qualifiedName, descriptor.id)
            assertEquals("NOP validation data type", descriptor.displayName)
            assertEquals("nop", descriptor.feature.id)
            assertEquals(60, config as Int)
        }
    }

    @Test
    fun `Validation stamp without data type`() {
        val branch = do_create_branch()
        // Creates a validation stamp with a data type
        val vsId = structureRepository.newValidationStamp(
                ValidationStamp.of(
                        branch,
                        NameDescription.nd("VS", "")
                )
        ).id
        // Loads the validation stamp
        val vs = structureRepository.getValidationStamp(vsId)
        assertEquals("VS", vs.name)
        assertNull(vs.dataType, "Data type null")
    }

}
