package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EventQueryServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private EventQueryService eventQueryService

    @Test
    void 'Branch creation'() {
        // Creates a branch
        Branch branch = doCreateBranch()
        // Gets the branch creation signature
        def o = asUserWithView(branch).call {
            eventQueryService.getLastEventSignature(branch.projectEntityType, branch.id, EventFactory.NEW_BRANCH)
        }
        assert o != null
        assert o.present
        assert o.get().user.name == 'user'
    }

}
