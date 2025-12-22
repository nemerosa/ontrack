package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLRootQueryPaginatedProjectsIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Paginated list of projects`() {
        // Removing all projects
        structureService.projectList.forEach { project -> structureService.deleteProject(project.id) }
        // Creating projects
        repeat(15) { no ->
            val name = "P${no.toString().padStart(2, '0')}"
            project(name = name)
        }
        // Querying with an offset
        run(
            """
                {
                    paginatedProjects(offset: 10) {
                        pageItems {
                            name
                        }
                    }
                }
            """.trimIndent()
        ) { data ->
            assertEquals(5, data["paginatedProjects"]["pageItems"].size())
            assertEquals("P10", data["paginatedProjects"]["pageItems"][0]["name"].asText())
        }
    }

    @Test
    @AsAdminTest
    fun `Filtering projects by name`() {
        // Removing all projects
        structureService.projectList.forEach { project -> structureService.deleteProject(project.id) }
        // Creating projects
        project(name = "Project A")
        project(name = "Project B")
        project(name = "Other")
        // Querying with a name filter
        run(
            """
                {
                    paginatedProjects(name: "Project") {
                        pageItems {
                            name
                        }
                    }
                }
            """.trimIndent()
        ) { data ->
            val items = data["paginatedProjects"]["pageItems"]
            assertEquals(2, items.size())
            val names = items.map { it["name"].asText() }.toSet()
            assertEquals(setOf("Project A", "Project B"), names)
        }
    }

}