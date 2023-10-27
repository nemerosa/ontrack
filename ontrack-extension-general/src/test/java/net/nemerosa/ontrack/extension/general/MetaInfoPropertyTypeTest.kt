package net.nemerosa.ontrack.extension.general

import com.nhaarman.mockitokotlin2.mock
import net.nemerosa.ontrack.extension.general.MetaInfoPropertyItem.Companion.of
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.*
import kotlin.test.assertNull

class MetaInfoPropertyTypeTest {

    private val type = MetaInfoPropertyType(
        extensionFeature = GeneralExtensionFeature(),
        searchIndexService = mock(),
        metaInfoSearchExtension = MetaInfoSearchExtension(
            extensionFeature = GeneralExtensionFeature(),
            uriBuilder = MockURIBuilder(),
            propertyService = mock(),
            structureService = mock()
        )
    )

    @Test
    fun `Search argument for when no name`() {
        val args = type.getSearchArguments("")
        assertNull(args, "No search on empty name")
    }

    @Test
    fun `Search argument for when name only`() {
        val args = type.getSearchArguments("name")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name", it.jsonCriteria)
            assertEquals(mapOf("name" to "name"), it.criteriaParams)
        }
    }

    @Test
    fun `Search argument for when name and null category`() {
        val args = type.getSearchArguments("/name")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'category' is null", it.jsonCriteria)
            assertEquals(mapOf("name" to "name"), it.criteriaParams)
        }
    }

    @Test
    fun `Search argument for when name and explicit category`() {
        val args = type.getSearchArguments("category/name")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'category' = :category", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "category" to "category",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and value`() {
        val args = type.getSearchArguments("name:value")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "value" to "value",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and value pattern`() {
        val args = type.getSearchArguments("name:value*")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "value" to "value%",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and null category and value`() {
        val args = type.getSearchArguments("/name:value")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value and item->>'category' is null", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "value" to "value",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and null category and value pattern`() {
        val args = type.getSearchArguments("/name:value*")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value and item->>'category' is null", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "value" to "value%",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and explicit category and value`() {
        val args = type.getSearchArguments("category/name:value")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value and item->>'category' = :category", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "category" to "category",
                    "value" to "value",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun `Search argument for when name and explicit category and value pattern`() {
        val args = type.getSearchArguments("category/name:value*")
        assertNotNull(args, "Search possible") {
            assertEquals("jsonb_array_elements(pp.json->'items') as item", it.jsonContext)
            assertEquals("item->>'name' = :name and item->>'value' ilike :value and item->>'category' = :category", it.jsonCriteria)
            assertEquals(
                mapOf(
                    "name" to "name",
                    "category" to "category",
                    "value" to "value%",
                ),
                it.criteriaParams
            )
        }
    }

    @Test
    fun containsValueNOKIfWrongFormat() {
        assertFalse(
            type.containsValue(
                MetaInfoProperty(
                    listOf(
                        of("name", "value")
                    )
                ), "value"
            )
        )
    }

    @Test
    fun containsValueNOKIfNotFound() {
        assertFalse(
            type.containsValue(
                MetaInfoProperty(
                    listOf(
                        of("name", "value1")
                    )
                ), "name:value"
            )
        )
    }

    @Test
    fun containsValueOKIfFound() {
        assertTrue(
            type.containsValue(
                MetaInfoProperty(
                    listOf(
                        of("name", "value1")
                    )
                ), "name:value1"
            )
        )
    }

    @Test
    fun containsValueOKIfFoundAmongOthers() {
        assertTrue(
            type.containsValue(
                MetaInfoProperty(
                    Arrays.asList(
                        of("name1", "value1"),
                        of("name2", "value2")
                    )
                ), "name2:value2"
            )
        )
    }
}