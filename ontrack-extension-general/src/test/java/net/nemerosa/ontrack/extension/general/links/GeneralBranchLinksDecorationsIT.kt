package net.nemerosa.ontrack.extension.general.links

import net.nemerosa.ontrack.it.links.AbstractBranchLinksTestSupport
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneralBranchLinksDecorationsIT : AbstractBranchLinksTestSupport() {

    @Test
    fun `Time decoration`() {
        withLinks {
            val component = build("component", 1)
            val library = build("library", 1)
            component linkTo library
            assertBuildLinks(component, BranchLinksDirection.USING) {
                assertEdge(library) {
                    assertDecoration("time") {
                        assertTrue(text.isNotBlank(), "Time is displayed")
                        assertEquals("time", icon)
                    }
                }
            }
        }
    }

}