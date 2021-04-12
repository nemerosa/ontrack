package net.nemerosa.ontrack.casc.schema

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class CascSchemaTest {

    @Test
    fun `CasC field from string`() {
        val field = cascField(Sample::requiredString)
        assertEquals("requiredString", field.name)
        assertEquals("Required string", field.description)
        assertSame(cascString, field.type)
        assertEquals(true, field.required)
    }

    @Test
    fun `CasC field from nullable string`() {
        val field = cascField(Sample::optionalString)
        assertEquals("optionalString", field.name)
        assertEquals("Optional string", field.description)
        assertSame(cascString, field.type)
        assertEquals(false, field.required)
    }

    @Test
    fun cascFieldName() {
        assertEquals(
            "maxBranches",
            cascFieldName(HomePageSettings::maxBranches)
        )
    }

    @Test
    fun cascFieldNameWithJsonProperty() {
        assertEquals(
            "grantProjectViewToAll",
            cascFieldName(SecuritySettings::isGrantProjectViewToAll)
        )
    }

    @Test
    fun cascObject() {
        val type = cascObject(HomePageSettings::class)
        assertIs<CascObject>(type) { o ->
            assertEquals("Settings to configure the home page.", o.description)
            assertNotNull(o.fields.find { it.name == "maxBranches" }) {
                assertSame(cascInt, it.type)
                assertEquals("Maximum of branches to display per favorite project", it.description)
                assertEquals(true, it.required)
            }
            assertNotNull(o.fields.find { it.name == "maxProjects" }) {
                assertSame(cascInt, it.type)
                assertEquals("Maximum of projects starting from which we need to switch to a search mode",
                    it.description)
                assertEquals(true, it.required)
            }
        }
    }

    class Sample(
        @APIDescription("Required string")
        val requiredString: String,
        @APIDescription("Optional string")
        val optionalString: String?,
    )

}