package net.nemerosa.ontrack.extension.casc.secrets

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CascSecretExpressionContextTest {

    @Test
    fun `Delegation to the secrets service 007`() {
        val secretService: CascSecretService = mockk()
        every { secretService.getValue("james") } returns "Bond"
        val context = CascSecretExpressionContext(secretService)
        assertEquals("Bond", context.evaluate("james"))
    }

}