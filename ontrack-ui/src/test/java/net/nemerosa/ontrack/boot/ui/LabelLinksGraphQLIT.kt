package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.labels.LabelManagement
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LabelLinksGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Links for admin`() {
        val label = label()
        val data = asUser().with(LabelManagement::class.java).call {
            run("""
           {
                labels {
                    id
                    category
                    name
                    links {
                        _update
                    }
                }
           }
            """.trimIndent())
        }
        val labels = data["labels"]
        val createdLabel = labels.find { it["id"].asInt() == label.id }
        assertNotNull(createdLabel) {
            assertEquals(label.category, it["category"].asText())
            assertEquals(label.name, it["name"].asText())
            val link = it["links"]["_update"].asText()
            assertEquals("urn:test:net.nemerosa.ontrack.boot.ui.LabelController#getUpdateLabelForm:${label.id}", link)
        }
    }

}