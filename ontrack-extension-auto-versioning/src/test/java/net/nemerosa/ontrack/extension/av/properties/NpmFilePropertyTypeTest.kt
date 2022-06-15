package net.nemerosa.ontrack.extension.av.properties

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NpmFilePropertyTypeTest {

    @Test
    fun `Replacement respects the indentation`() {
        val initial = """
            {
              "name" : "@nemerosa/testing",
              "dependencies": {
                "@nemerosa/module-1" : "^4.0.1",
                "@nemerosa/module-2" : "^1.0.0",
                "@nemerosa/module-3" : "^7.3.1",
                "@nemerosa/module-4" : "^5.1.2"
              }
            }
        """.trimIndent().lines()
        val type: FilePropertyType = NpmFilePropertyType()

        // Reading
        assertEquals("7.3.1", type.readProperty(initial, "@nemerosa/module-3"))

        // Replacing
        val replaced = type.replaceProperty(initial, "@nemerosa/module-3", "7.3.2").joinToString("\n")

        // Checking the final result
        assertEquals(
            """
                {
                  "name" : "@nemerosa/testing",
                  "dependencies" : {
                    "@nemerosa/module-1" : "^4.0.1",
                    "@nemerosa/module-2" : "^1.0.0",
                    "@nemerosa/module-3" : "^7.3.2",
                    "@nemerosa/module-4" : "^5.1.2"
                  }
                }
            """.trimIndent().trim(),
            replaced.trim()
        )
    }

}