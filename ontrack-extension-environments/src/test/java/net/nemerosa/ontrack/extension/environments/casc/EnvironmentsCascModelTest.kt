package net.nemerosa.ontrack.extension.environments.casc

import io.mockk.mockk
import org.junit.jupiter.api.Test

class EnvironmentsCascModelTest {

    @Test
    fun `Casc type`() {
        val context = EnvironmentsCascContext(
            environmentService = mockk()
        )
        val type = context.type
        println(type)
    }

}