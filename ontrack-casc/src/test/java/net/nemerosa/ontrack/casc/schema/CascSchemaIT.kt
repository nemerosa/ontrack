package net.nemerosa.ontrack.casc.schema

import net.nemerosa.ontrack.casc.context.OntrackContext
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CascSchemaIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var ontrackContext: OntrackContext

    @Test
    fun `Overall schema`() {
        val root = ontrackContext.type
        assertIs<CascObject>(root)
    }

}