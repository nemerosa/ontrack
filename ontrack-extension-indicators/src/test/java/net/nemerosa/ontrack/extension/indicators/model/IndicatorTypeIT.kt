package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.*

class IndicatorTypeIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Creating a type which already exists`() {
        val category = category()
        val type = category.booleanType()
        asUserWith<IndicatorTypeManagement> {
            assertFailsWith<IndicatorTypeIdAlreadyExistsException> {
                indicatorTypeService.createType(
                    CreateTypeForm(
                        id = type.id,
                        category = category.id,
                        name = "Another type with same ID",
                        link = null,
                        valueType = ServiceConfiguration(
                            id = booleanIndicatorValueType.id,
                            data = booleanIndicatorValueType.toConfigForm(BooleanIndicatorValueTypeConfig(true))
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Creating a type which already exists (direct method)`() {
        val category = category()
        val type = category.booleanType()
        asUserWith<IndicatorTypeManagement> {
            assertFailsWith<IndicatorTypeIdAlreadyExistsException> {
                indicatorTypeService.createType(
                    id = type.id,
                    category = category,
                    name = "Another type with same ID",
                    link = null,
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(true),
                    source = null,
                    computed = false
                )
            }
        }
    }

    @Test
    fun `Creating a type using a form`() {
        val category = category()
        val typeId = uid("T")
        asUserWith<IndicatorTypeManagement> {
            indicatorTypeService.createType(
                CreateTypeForm(
                    id = typeId,
                    category = category.id,
                    name = "Another type",
                    link = null,
                    valueType = ServiceConfiguration(
                        id = booleanIndicatorValueType.id,
                        data = booleanIndicatorValueType.toConfigForm(BooleanIndicatorValueTypeConfig(true))
                    )
                )
            )
            // Gets the type back
            indicatorTypeService.getTypeById(typeId).apply {
                assertEquals(category, this.category)
                assertEquals("Another type", name)
                assertSame(booleanIndicatorValueType, valueType)
                assertEquals(BooleanIndicatorValueTypeConfig(true), valueConfig)
            }
        }
    }

    @Test
    fun `Updating a type using a form`() {
        val category = category()
        val type = category.booleanType()
        asUserWith<IndicatorTypeManagement> {
            indicatorTypeService.updateType(
                CreateTypeForm(
                    id = type.id,
                    category = category.id,
                    name = "Another type",
                    link = null,
                    valueType = ServiceConfiguration(
                        id = booleanIndicatorValueType.id,
                        data = booleanIndicatorValueType.toConfigForm(BooleanIndicatorValueTypeConfig(false))
                    )
                )
            )
            // Gets the type back
            indicatorTypeService.getTypeById(type.id).apply {
                assertEquals(category, this.category)
                assertEquals("Another type", name)
                assertSame(booleanIndicatorValueType, valueType)
                assertEquals(BooleanIndicatorValueTypeConfig(false), valueConfig)
            }
        }
    }

    @Test
    fun `Deleting a category deletes the associated types`() {
        val category = category()
        val types = (1..3).map { category.booleanType() }
        val ids = types.map { it.id }

        // Checks we can find the types back
        assertTrue(ids.all { id ->
            indicatorTypeService.findTypeById(id) != null
        })

        // Deletes the category
        asAdmin {
            indicatorCategoryService.deleteCategory(category.id)
        }

        // Checks the types are gone
        assertTrue(ids.all { id ->
            indicatorTypeService.findTypeById(id) == null
        })
    }

    @Test
    fun `Deleting a non existing type`() {
        asAdmin {
            val result = indicatorTypeService.deleteType("xxx")
            assertFalse(result.isSuccess)
        }
    }

    @Test
    fun `Type cannot be deleted if there is a source and no deprecation reason`() {
        asAdmin {
            val type = category().booleanType(
                source = source()
            )
            assertFalse(indicatorTypeService.deleteType(type.id).isSuccess, "Type was not deleted")
            assertNotNull(indicatorTypeService.findTypeById(type.id), "Type was not deleted")
        }
    }

    @Test
    fun `Category cannot be deleted if there is a source and a blank deprecation reason`() {
        asAdmin {
            val type = category().booleanType(
                source = source(),
                deprecated = ""
            )
            assertFalse(indicatorTypeService.deleteType(type.id).isSuccess, "Type was not deleted")
            assertNotNull(indicatorTypeService.findTypeById(type.id), "Type was not deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is a source but a deprecation reason`() {
        asAdmin {
            val type = category().booleanType(
                source = source(),
                deprecated = "Obsolete"
            )
            assertTrue(indicatorTypeService.deleteType(type.id).isSuccess, "Type was deleted")
            assertNull(indicatorTypeService.findTypeById(type.id), "Type has been deleted")
        }
    }

    @Test
    fun `Category can be deleted if there is no source`() {
        asAdmin {
            val type = category().booleanType()
            assertTrue(indicatorTypeService.deleteType(type.id).isSuccess, "Type was deleted")
            assertNull(indicatorTypeService.findTypeById(type.id), "Type has been deleted")
        }
    }

}