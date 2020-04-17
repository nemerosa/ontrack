package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EventQueryServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var eventQueryService: EventQueryService

    @Test
    fun `Branch creation`() {
        // Creates a branch
        val branch = doCreateBranch()
        // Gets the branch creation signature
        val o = asUserWithView(branch).call {
            eventQueryService.getLastEventSignature(branch.projectEntityType, branch.id, EventFactory.NEW_BRANCH)
        }

        assertPresent(o) {}
    }

}
