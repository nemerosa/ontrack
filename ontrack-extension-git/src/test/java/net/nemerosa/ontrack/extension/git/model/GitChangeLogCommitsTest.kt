package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.git.model.plot.GPlot
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.PromotionRunFixtures
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNotNull
import net.nemerosa.ontrack.test.assertJsonNull
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapperFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitChangeLogCommitsTest {

    private lateinit var mapper: ResourceObjectMapper

    @BeforeEach
    fun init() {
        val securityService = MockSecurityService()
        mapper = ResourceObjectMapperFactory().resourceObjectMapper(
            DefaultResourceContext(MockURIBuilder(), securityService),
        )
    }

    @Test
    fun `Rendering of build decorations without branches`() {
        val buildName = uid("b-")
        val build = BuildFixtures.testBuild(name = buildName)
        val commits = GitChangeLogCommits(
            log = GitUILog(
                plot = GPlot(),
                commits = listOf(
                    GitCommitFixtures.testGitUICommit(build = build),
                )
            )
        )
        val json = mapper.write(commits).parseAsJson()
        // First commit
        assertJsonNotNull(json.path("log").path("commits").path(0)) {
            // Build node
            assertJsonNotNull(path("build")) {
                // Checks the name is alright
                assertEquals(buildName, getRequiredTextField("name"))
                // Checks no branch field is set
                assertJsonNull(path("branch"), "Branch field is not set")
            }
        }
    }

    @Test
    fun `Rendering of promotion run decorations without builds but with promotion levels`() {
        val promotionName = uid("PL")
        val promotionRun = PromotionRunFixtures.testPromotionRun(promotionLevelName = promotionName)
        val commits = GitChangeLogCommits(
            log = GitUILog(
                plot = GPlot(),
                commits = listOf(
                    GitCommitFixtures.testGitUICommit(
                        promotions = listOf(promotionRun),
                    ),
                )
            )
        )
        val json = mapper.write(commits).parseAsJson()
        // First commit
        assertJsonNotNull(json.path("log").path("commits").path(0)) {
            // Promotion runs nodes
            assertJsonNotNull(path("promotions"), "Promotions field") {
                // First promotion run
                assertJsonNotNull(path(0), "One promotion") {
                    // No build
                    assertJsonNull(path("build"), "No build")
                    // ... but the promotion level
                    assertJsonNotNull(path("promotionLevel"), "Promotion level") {
                        // Checking the name
                        assertEquals(promotionName, getRequiredTextField("name"))
                    }
                }
            }
        }
    }

}