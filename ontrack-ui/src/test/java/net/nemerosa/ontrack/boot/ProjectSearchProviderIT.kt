package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProjectSearchProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var projectSearchProvider: ProjectSearchProvider

    @Test
    fun `Project search by exact name`() {
        val project = project {}
        val results = projectSearchProvider.search(project.name)
        val result = results.find {
            it.title == "Project ${project.name}"
        }
        assertNotNull(result)
    }

    @Test
    fun `Project search by short token does not return any result`() {
        val prefix = uid("PP")
        // Creates three projects with the same prefix
        (1..3).map { doCreateProject(NameDescription.nd(uid(prefix), "")) }
        // Search with short prefix (<=3) does not return anything
        val results = projectSearchProvider.search("PP")
        assertTrue(results.isEmpty(), "No result")
    }

    @Test
    fun `Project search by content`() {
        val prefix = uid("PP")
        // Creates three projects with the same prefix
        (1..3).map { doCreateProject(NameDescription.nd(uid(prefix), "")) }
        // Search with short prefix (<=3) does not return anything
        val results = projectSearchProvider.search(prefix)
        assertEquals(3, results.size)
    }

}