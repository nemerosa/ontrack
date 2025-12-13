package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.ValidationStampService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class ValidationStampServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: ValidationStampService

    @Test
    fun `Finding validation stamps from a list of names`() {
        project {
            branch {
                val vs1 = validationStamp()
                /* val vs2 = */ validationStamp()
                val vs3 = validationStamp()

                val vsList = service.findValidationStampsForNames(
                    branch = this,
                    validationStamps = listOf(vs1.name, vs3.name),
                )
                assertEquals(
                    listOf(vs1, vs3).sortedBy { it.name },
                    vsList,
                )
            }
        }
    }

}