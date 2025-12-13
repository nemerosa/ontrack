package net.nemerosa.ontrack.graphql.actuator

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class JsonSchemaActuatorControllerIT(
    @Autowired
    private val jsonSchemaActuatorController: JsonSchemaActuatorController,
): AbstractDSLTestSupport() {

    @Test
    fun schemaJson() {
        val node = jsonSchemaActuatorController.schemaJson()
        assertJsonNotNull(node, "Json schema has been generated")
    }

}