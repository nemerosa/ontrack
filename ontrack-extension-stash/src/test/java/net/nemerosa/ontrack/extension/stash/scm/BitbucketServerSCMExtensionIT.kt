package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.extension.git.GitTestSupport
import net.nemerosa.ontrack.extension.stash.BitbucketServerTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BitbucketServerSCMExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var bitbucketServerTestSupport: BitbucketServerTestSupport

    @Autowired
    private lateinit var gitTestSupport: GitTestSupport

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Test
    fun `Event based path expansion with SCM branch`() {
        asAdmin {
            withDisabledConfigurationTest {
                bitbucketServerTestSupport.withBitbucketServerConfig { config ->
                    project {
                        bitbucketServerTestSupport.setStashProjectProperty(this, config, "MYPRJ", "myrepo")
                        branch {
                            gitTestSupport.setGitBranchConfigurationProperty(this, "any/branch")
                            // Creating an event
                            val pl = promotionLevel()
                            build {
                                val run = promote(pl)
                                // Event
                                val event = eventFactory.newPromotionRun(run)
                                // Rendering
                                val path = eventTemplatingService.renderEvent(
                                    event = event,
                                    context = emptyMap(),
                                    template = "/my/path/${'$'}{branch.scmBranch|urlencode}",
                                    renderer = PlainEventRenderer.INSTANCE,
                                )
                                // Check
                                assertEquals(
                                    "/my/path/any%2Fbranch",
                                    path
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}