package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.IntegerIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.PercentageIndicatorValueType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class IndicatorValueTypeServiceIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var indicatorValueTypeService: IndicatorValueTypeService

    @Test
    fun `Find all`() {
        val types = indicatorValueTypeService.findAll()
        assertTrue(types.any { it.id == BooleanIndicatorValueType::class.java.name })
        assertTrue(types.any { it.id == IntegerIndicatorValueType::class.java.name })
        assertTrue(types.any { it.id == PercentageIndicatorValueType::class.java.name })
    }

    @Test
    fun `Find by ID`() {
        assertNull(indicatorValueTypeService.findValueTypeById<Any, Any>("xxx"))
        assertNotNull(indicatorValueTypeService.findValueTypeById<Any, Any>(BooleanIndicatorValueType::class.java.name))
    }

    @Test
    fun `Get by ID`() {
        assertFailsWith<IndicatorValueTypeNotFoundException> {
            indicatorValueTypeService.getValueType<Any, Any>("xxx")
        }
        indicatorValueTypeService.getValueType<Any, Any>(BooleanIndicatorValueType::class.java.name).apply {
            assertEquals("Yes/No", name)
        }
    }

}