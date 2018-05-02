package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests around the `validationStamp` root query.
 */
class ValidationStampGraphQLIT : AbstractQLKTITSupport() {


    @Test
    fun `No validation stamp`() {
        val data = run("""{
            validationStamp(id: 1) {
                name
            }
        }""")

        val vs = data["validationStamp"]
        assertTrue(vs.isNull, "No validation stamp")
    }


    @Test
    fun `Validation stamp by ID`() {
        val vs = doCreateValidationStamp()
        val data = run("""{
            validationStamp(id: ${vs.id}) {
                name
            }
        }""")

        val name = data["validationStamp"]["name"].asText()
        assertEquals(vs.name, name)
    }

}