package net.nemerosa.ontrack.kdsl.acceptance.tests.core

import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Testing the "/rest/structure/entity" HTTP calls.
 */
class ACCRestEntity : AbstractACCDSLTestSupport() {

    @Test
    fun `Getting a project by name`() {
        project {
            val json = ontrack.connector.get("/rest/structure/entity/project/$name").body.asJson()
            assertEquals(
                id.toInt(),
                json.getRequiredIntField("id")
            )
        }
    }

    @Test
    fun `Getting a branch by name`() {
        project {
            branch {
                val json = ontrack.connector.get("/rest/structure/entity/branch/${project.name}/$name").body.asJson()
                assertEquals(
                    id.toInt(),
                    json.getRequiredIntField("id")
                )
            }
        }
    }

    @Test
    fun `Getting a validation stamp by name`() {
        project {
            branch {
                val vs = validationStamp(name = "Some validation stamp")
                val json =
                    ontrack.connector.get("/rest/structure/entity/validationStamp/${project.name}/$name/${vs.name}").body.asJson()
                assertEquals(
                    vs.id.toInt(),
                    json.getRequiredIntField("id")
                )
            }
        }
    }

}