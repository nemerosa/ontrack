package net.nemerosa.ontrack.extension.indicators.imports

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class IndicatorImportsControllerTest {

    @Test
    fun check() {
        val importsService: IndicatorImportsService = mockk(relaxed = true)
        val controller = IndicatorImportsController(importsService)
        val data = IndicatorImports("source", emptyList())
        controller.imports(data)
        verify {
            importsService.imports(data)
        }
    }

}