package net.nemerosa.ontrack.extension.indicators.imports

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

class IndicatorImportsControllerTest {

    @Test
    fun check() {
        val importsService: IndicatorImportsService = mock()
        val controller = IndicatorImportsController(importsService)
        val data = IndicatorImports("source", emptyList())
        controller.imports(data)
        verify(importsService).imports(data)
    }

}