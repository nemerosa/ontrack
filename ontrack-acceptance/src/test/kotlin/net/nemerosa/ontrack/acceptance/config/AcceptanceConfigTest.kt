package net.nemerosa.ontrack.acceptance.config

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import org.junit.Test
import kotlin.reflect.full.findAnnotation
import kotlin.test.assertTrue

class AcceptanceConfigTest {

    @Test
    fun `Test accepted by default when no context`() {
        val config = AcceptanceConfig()
        assertTrue(config.acceptTest(AnyContext::class.findAnnotation()))
    }

    @AcceptanceTest("anyContext")
    class AnyContext

}