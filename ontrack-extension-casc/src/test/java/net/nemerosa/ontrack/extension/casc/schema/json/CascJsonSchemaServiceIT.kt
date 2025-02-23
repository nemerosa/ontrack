package net.nemerosa.ontrack.extension.casc.schema.json

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test

class CascJsonSchemaServiceIT : AbstractCascTestSupport() {

    @Test
    fun `Creating the Casc JSON schema`() {
        val schema = asAdmin {
            cascJsonSchemaService.createCascJsonSchema()
        }
        println(schema.toPrettyString())
    }

    @Test
    fun `Validation of security settings`() {
        assertValidYaml(
            """
                ontrack:
                  config:
                    settings:
                      security:
                        builtInAuthenticationEnabled: false
                        grantProjectViewToAll: true
                        grantProjectParticipationToAll: true
                        grantDashboardEditionToAll: true
                        grantDashboardSharingToAll: true
            """.trimIndent()
        )
    }

}