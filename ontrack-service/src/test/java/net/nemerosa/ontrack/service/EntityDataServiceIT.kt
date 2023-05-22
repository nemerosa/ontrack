package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EntityDataServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var entityDataService: EntityDataService

    @Test
    fun retrieve_authorized_for_view() {
        project {
            // Key
            val key = TestUtils.uid("K")
            // Stores some data
            asAdmin {
                entityDataService.store(this, key, "Value 1")
            }
            // Retrieves it using view right only
            val value = asUser().withView(project).call {
                entityDataService.retrieve(project, key)
            }
            // Cheks
            assertEquals("Value 1", value)
        }
    }
}
