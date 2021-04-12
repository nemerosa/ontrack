package net.nemerosa.ontrack.graphql.schema.casc

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.Test

class CascGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Accessing the Casc schema`() {
        asAdmin {
            run("""
                {
                    casc {
                        schema
                    }
                }
            """).let { data ->
                val schema = data.path("casc").path("schema")
                assertJsonNotNull(schema)
            }
        }
    }
}