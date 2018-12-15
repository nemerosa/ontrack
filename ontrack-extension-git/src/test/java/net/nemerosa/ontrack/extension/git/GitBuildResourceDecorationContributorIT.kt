package net.nemerosa.ontrack.extension.git

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.it.ResourceDecorationContributorTestSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.ui.resource.ResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GitBuildResourceDecorationContributorIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var resourceModule: ResourceModule

    @Autowired
    private lateinit var contributor: GitBuildResourceDecorationContributor

    private lateinit var resourceObjectMapper: ResourceObjectMapper

    @Before
    fun setup() {
        resourceObjectMapper = ResourceDecorationContributorTestSupport.createResourceObjectMapper(
                Build::class.java,
                contributor,
                securityService
        )
    }

    @Test
    fun `No change log link on a build not configured`() {
        // Creates a build
        val build = doCreateBuild()

        val node = asUserWithView(build).call {
            resourceObjectMapper.objectMapper.valueToTree<JsonNode>(build)
        }
        assertNull(node.get("_changeLog"))
        assertNull(node.get("_changeLogPage"))
    }

    @Test
    fun `Change log link on a build`() {
        createRepo {
            commits(4)
        } and { repo, _ ->
            project {
                gitProject(repo)
                branch {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    build {
                        val node = asUserWithView(this).call {
                            resourceObjectMapper.objectMapper.valueToTree<JsonNode>(this)
                        }
                        assertEquals(
                                "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLog:BuildDiffRequest%28from%3D$id%2C+to%3Dnull%29",
                                node.get("_changeLog").asText()
                        )
                        assertEquals(
                                "urn:test:#:extension/git/changelog",
                                node.get("_changeLogPage").asText()
                        )
                    }
                }
            }
        }
    }
}
