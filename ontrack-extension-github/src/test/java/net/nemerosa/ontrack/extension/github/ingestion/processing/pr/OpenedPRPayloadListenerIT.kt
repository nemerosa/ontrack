package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestJUnit4Support
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OpenedPRPayloadListenerIT : AbstractIngestionTestJUnit4Support() {

    @Autowired
    private lateinit var openedPRPayloadListener: OpenedPRPayloadListener

    @Test
    fun `Upon receiving an opened PR event, we create a PR branch`() {
        val name = uid("p")
        val payload = PRPayloadFixtures.payload(repoName = name, action = PRPayloadAction.opened)
        asAdmin {
            onlyOneGitHubConfig()
            openedPRPayloadListener.process(payload, null)
            // Checks the project & the branch have been created
            assertNotNull(structureService.findProjectByName(name).getOrNull(), "Project has been created") {
                assertNotNull(structureService.findBranchByName(name, "PR-1").getOrNull(),
                    "PR branch has been created") { branch ->
                    assertNotNull(getProperty(branch, GitBranchConfigurationPropertyType::class.java),
                        "PR branch configured for Git") { property ->
                        assertEquals("PR-1", property.branch)
                    }
                }
            }
        }
    }

}