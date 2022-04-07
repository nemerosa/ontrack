package net.nemerosa.ontrack.extension.ldap.casc

import net.nemerosa.ontrack.extension.casc.schema.CascObject
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.ldap.LDAPSettings
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LDAPSettingsContextTest {

    @Test
    fun `Casc type`() {
        val type = cascObject(LDAPSettings::class)
        assertIs<CascObject>(type) { ldap ->
            assertNotNull(ldap.fields.find { it.name == "enabled" }) { enabledField ->
                assertEquals("boolean", enabledField.type.__type)
            }
        }
    }

}