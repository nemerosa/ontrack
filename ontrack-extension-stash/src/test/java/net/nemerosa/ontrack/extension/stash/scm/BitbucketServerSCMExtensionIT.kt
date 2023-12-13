package net.nemerosa.ontrack.extension.stash.scm

import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.extension.git.GitTestSupport
import net.nemerosa.ontrack.extension.stash.BitbucketServerTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventVariableService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BitbucketServerSCMExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var bitbucketServerTestSupport: BitbucketServerTestSupport

    @Autowired
    private lateinit var gitTestSupport: GitTestSupport

    @Autowired
    private lateinit var eventVariableService: EventVariableService

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
                                // Gets the parameters
                                val templateParameters =
                                    eventVariableService.getTemplateParameters(event, caseVariants = true)
                                // Path expansion
                                val path = SimpleExpand.expand("/my/path/{ScmBranch|urlencode}", templateParameters)
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