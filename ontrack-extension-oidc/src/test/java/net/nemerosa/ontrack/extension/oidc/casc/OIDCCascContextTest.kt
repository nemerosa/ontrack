package net.nemerosa.ontrack.extension.oidc.casc

import io.mockk.mockk
import net.nemerosa.ontrack.casc.schema.CascArray
import net.nemerosa.ontrack.casc.schema.CascObject
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test

class OIDCCascContextTest {

    @Test
    fun `OIDC CasC schema type`() {
        val oidcCascContext = OIDCCascContext(mockk())
        val type = oidcCascContext.type
        assertIs<CascArray>(type) { array ->
            assertIs<CascObject>(array.type)
        }
    }

}