package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IndicatorImportsIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var indicatorImportsService: IndicatorImportsService

    @Test
    fun `Importing categories and types`() {
        val source = uid("S")
        val prefix = uid("P")
        val data = IndicatorImports(
                source = source,
                categories = listOf(
                        IndicatorImportCategory(
                                id = "$prefix-cat-one",
                                name = "Category 1",
                                types = listOf(
                                        IndicatorImportsType(
                                                id = "$prefix-type-one",
                                                name = "Type 1",
                                                link = null,
                                                required = false
                                        ),
                                        IndicatorImportsType(
                                                id = "$prefix-type-two",
                                                name = "Type 2",
                                                link = "https://github.com/nemerosa/ontrack",
                                                required = true
                                        )
                                )
                        )
                )
        )
        asAdmin {
            indicatorImportsService.imports(data)
        }

        val expectedSource = IndicatorSource(
                name = source,
                provider = IndicatorSourceProviderDescription(
                        id = ImportsIndicatorSourceProvider::class.java.name,
                        name = "Import"
                )
        )

        // Checks category
        assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-one")) { category ->
            assertEquals("$prefix-cat-one", category.id)
            assertEquals("Category 1", category.name)
            assertEquals(expectedSource, category.source)
        }

        // Check types
        assertNotNull(indicatorTypeService.findTypeById("$prefix-type-one")) { type ->
            assertEquals("$prefix-type-one", type.id)
            assertEquals("Type 1", type.name)
            assertEquals(null, type.link)
            assertIs<BooleanIndicatorValueType>(type.valueType) {}
            assertEquals(BooleanIndicatorValueTypeConfig(false), type.valueConfig)
            assertEquals(expectedSource, type.source)
        }
        assertNotNull(indicatorTypeService.findTypeById("$prefix-type-two")) { type ->
            assertEquals("$prefix-type-two", type.id)
            assertEquals("Type 2", type.name)
            assertEquals("https://github.com/nemerosa/ontrack", type.link)
            assertIs<BooleanIndicatorValueType>(type.valueType) {}
            assertEquals(BooleanIndicatorValueTypeConfig(true), type.valueConfig)
            assertEquals(expectedSource, type.source)
        }
    }

}