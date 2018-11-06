package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PredefinedValidationStampRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: PredefinedValidationStampRepository

    @Test
    fun `Predefined validation stamp with a null description`() {
        val name = uid("PVS")
        val pvsId = repository.newPredefinedValidationStamp(
                PredefinedValidationStamp.of(
                        NameDescription.nd(
                                name,
                                null
                        )
                )
        )
        val pvs = repository.getPredefinedValidationStamp(pvsId)
        assertEquals("", pvs.description)
    }

}