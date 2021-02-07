package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.IndicatorConfigProperties
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProviderDescription
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class IndicatorImportsIT : AbstractIndicatorsTestSupport() {

    @Autowired
    private lateinit var indicatorImportsService: IndicatorImportsService

    @Autowired
    private lateinit var indicatorConfigProperties: IndicatorConfigProperties

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

    @Test
    fun `Management of orphans using the source of the import using deletion as a configuration`() {
        val old = indicatorConfigProperties.importing.deleting
        try {
            indicatorConfigProperties.importing.deleting = true
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
                    ),
                    IndicatorImportCategory(
                        id = "$prefix-cat-two",
                        name = "Category 2",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-three",
                                name = "Type 3",
                                link = null,
                                required = false
                            ),
                            IndicatorImportsType(
                                id = "$prefix-type-four",
                                name = "Type 4",
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

            // Checks the imported data
            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-one"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-one"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-two"))

            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-two"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-three"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-four"))

            // Removes some types, adds some types and categories
            val newData = IndicatorImports(
                source = source,
                categories = listOf(
                    IndicatorImportCategory(
                        id = "$prefix-cat-one",
                        name = "Category 1",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-two",
                                name = "Type 2",
                                link = "https://github.com/nemerosa/ontrack",
                                required = true
                            ),
                            IndicatorImportsType(
                                id = "$prefix-type-five",
                                name = "Type 5",
                                link = "https://github.com/nemerosa/ontrack",
                                required = true
                            )
                        )
                    ),
                    IndicatorImportCategory(
                        id = "$prefix-cat-three",
                        name = "Category 3",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-six",
                                name = "Type 6",
                                link = null,
                                required = false
                            )
                        )
                    )
                )
            )
            asAdmin {
                indicatorImportsService.imports(newData)
            }

            // Checks the imported data
            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-one"))
            assertNull(indicatorTypeService.findTypeById("$prefix-type-one"), "Type should be gone")
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-two"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-five"))

            assertNull(indicatorCategoryService.findCategoryById("$prefix-cat-two"), "Category should be gone")
            assertNull(indicatorTypeService.findTypeById("$prefix-type-three"), "Type should be gone")
            assertNull(indicatorTypeService.findTypeById("$prefix-type-four"), "Type should be gone")

            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-three"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-six"))
        } finally {
            indicatorConfigProperties.importing.deleting = old
        }
    }

    @Test
    fun `Management of orphans using the source of the import using deprecation as a configuration`() {
        val old = indicatorConfigProperties.importing.deleting
        assertFalse(old, "We expect the deprecation mode to be true by default")
        try {
            indicatorConfigProperties.importing.deleting = false
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
                    ),
                    IndicatorImportCategory(
                        id = "$prefix-cat-two",
                        name = "Category 2",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-three",
                                name = "Type 3",
                                link = null,
                                required = false
                            ),
                            IndicatorImportsType(
                                id = "$prefix-type-four",
                                name = "Type 4",
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

            // Checks the imported data
            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-one"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-one"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-two"))

            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-two"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-three"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-four"))

            // Removes some types, adds some types and categories
            val newData = IndicatorImports(
                source = source,
                categories = listOf(
                    IndicatorImportCategory(
                        id = "$prefix-cat-one",
                        name = "Category 1",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-two",
                                name = "Type 2",
                                link = "https://github.com/nemerosa/ontrack",
                                required = true
                            ),
                            IndicatorImportsType(
                                id = "$prefix-type-five",
                                name = "Type 5",
                                link = "https://github.com/nemerosa/ontrack",
                                required = true
                            )
                        )
                    ),
                    IndicatorImportCategory(
                        id = "$prefix-cat-three",
                        name = "Category 3",
                        types = listOf(
                            IndicatorImportsType(
                                id = "$prefix-type-six",
                                name = "Type 6",
                                link = null,
                                required = false
                            )
                        )
                    )
                )
            )
            asAdmin {
                indicatorImportsService.imports(newData)
            }

            // Checks the imported data
            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-one"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-one")) {
                assertEquals("Deprecated because not part of the $source import source.", it.deprecated)
            }
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-two"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-five"))

            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-two")) {
                assertEquals("Deprecated because not part of the $source import source.", it.deprecated)
            }
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-three"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-four"))

            assertNotNull(indicatorCategoryService.findCategoryById("$prefix-cat-three"))
            assertNotNull(indicatorTypeService.findTypeById("$prefix-type-six"))
        } finally {
            indicatorConfigProperties.importing.deleting = old
        }
    }

}