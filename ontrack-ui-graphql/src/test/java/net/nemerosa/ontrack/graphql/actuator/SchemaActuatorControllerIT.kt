package net.nemerosa.ontrack.graphql.actuator

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class SchemaActuatorControllerIT(
    @Autowired
    private val schemaActuatorController: SchemaActuatorController,
) : AbstractDSLTestSupport() {

    @Test
    fun schemaJson() {
        val dsl = schemaActuatorController.schemaDsl()
        assertTrue(dsl.isNotBlank(), "DSL schema has been generated")
    }

}