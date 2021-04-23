package net.nemerosa.ontrack.extension.casc.ui

import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.casc.CascLoadingService
import org.junit.Test

class CascControllerTest {

    @Test
    fun reload() {
        val service: CascLoadingService = mockk(relaxed = true)
        val controller = CascController(service)

        controller.reload()

        verify {
            service.load()
        }
    }

}