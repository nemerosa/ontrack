package net.nemerosa.ontrack.extension.av.properties

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TomlFilePropertyTypeTest {

    private val tomlFilePropertyType = TomlFilePropertyType()

    @Test
    fun `Reading a property`() {
        assertEquals("1.0.0", tomlFilePropertyType.readProperty(toml, "global.myVersion"))
    }

    @Test
    fun `Reading a missing property returns null`() {
        assertEquals(null, tomlFilePropertyType.readProperty(toml, "global.missingProperty"))
    }

    @Test
    fun `Setting a property`() {
        val content = tomlFilePropertyType.replaceProperty(toml, "global.myVersion", "2.0.0")
        assertEquals(
            """
                [global]
                myVersion = "2.0.0"
            """.trimIndent(),
            content.trim()
        )
    }

    @Test
    fun `Setting a non existing property`() {
        val content = tomlFilePropertyType.replaceProperty(toml, "mySection.myVersion", "2.0.0")
        assertEquals(
            """
                [global]
                myVersion = "1.0.0"
                
                [mySection]
                myVersion = "2.0.0"
            """.trimIndent(),
            content.trim()
        )
    }

    companion object {
        val toml = """
            # Some comment
            [global]
            myVersion = "1.0.0"
        """.trimIndent()
    }

}