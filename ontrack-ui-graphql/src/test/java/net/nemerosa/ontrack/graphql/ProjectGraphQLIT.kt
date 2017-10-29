package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProjectGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Project by name when not authorized must throw an authentication exception`() {
        // Creates a project
        val project = doCreateProject()
        // Looks for this project by name, with a not authorized user
        assertFailsWith(AccessDeniedException::class, "Access denied") {
            withNoGrantViewToAll {
                asUser().call {
                    run("""{
                |  projects(name: "${project.name}") {
                |    id
                |  }
                |}""".trimMargin())
                }
            }
        }
    }

    @Test
    fun `Last promoted build`() {
        // Creating a promotion level
        val pl = doCreatePromotionLevel()
        // Creating a first promoted build
        val build1 = doCreateBuild(pl.branch, NameDescription.nd("1", ""))
        doPromote(build1, pl, "One")
        // Creating a second promoted build
        val build2 = doCreateBuild(pl.branch, NameDescription.nd("2", ""))
        doPromote(build2, pl, "Two")
        // Run a GraphQL query at project level and gets the last promotion run
        val data = run("""{
            |   projects(id: ${pl.project.id}) {
            |      branches {
            |          promotionLevels {
            |              name
            |              promotionRuns(first: 1) {
            |                build {
            |                  name
            |                }
            |              }
            |          }
            |      }
            |   }
            |}
        """.trimMargin())
        // Checks that the build associated with the promotion is the last one
        val plNode = data["projects"][0]["branches"][0]["promotionLevels"][0]
        assertEquals(pl.name, plNode["name"].asText())
        val runNodes = plNode["promotionRuns"]
        assertEquals(1, runNodes.size())
        val build = runNodes[0]["build"]
        assertEquals(build2.name, build["name"].asText())
    }

}