package net.nemerosa.ontrack.graphql

import org.junit.Test
import kotlin.test.assertNotNull

class LabelGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Schema OK`() {
        val data = run("""
           {
                labels {
                    category
                    name
                }
           }
        """)
        val labels = data["labels"]
        assertNotNull(labels)
    }

}