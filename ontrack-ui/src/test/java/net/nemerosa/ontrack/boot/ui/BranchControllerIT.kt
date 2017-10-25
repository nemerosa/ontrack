package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.security.BranchCreate
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BranchControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var controller: BranchController

    @Autowired
    private lateinit var eventQueryService: EventQueryService

    @Test
    fun createBranch() {
        // Project
        val project = doCreateProject()
        // Branch
        val nameDescription = nameDescription().asState()
        val branch = asUser()
                .with(project.id(), BranchCreate::class.java)
                .call {
                    controller.newBranch(project.id, nameDescription)
                }
        // Checks the branch
        checkBranchResource(branch, nameDescription)
    }

    @Test
    fun disablingEnablingBranch() {
        val branch = doCreateBranch()
        // Disables it
        val disabled = asUser().with(branch, ProjectEdit::class.java).call { controller.disableBranch(branch.id) }
        assertTrue(disabled.isDisabled, "Branch is disabled")
        val disabledEvent = eventQueryService.getLastEvent(branch, EventFactory.DISABLE_BRANCH)
        assertNotNull(disabledEvent, "Disabled event is there") { event ->
            val eventBranch: Branch? = event.getEntity(ProjectEntityType.BRANCH)
            assertNotNull(eventBranch, "Branch associated to the event") {
                assertEquals(branch.id, it.id)
            }
        }
        // Enables it
        val enabled = asUser().with(branch, ProjectEdit::class.java).call { controller.enableBranch(branch.id) }
        assertFalse(enabled.isDisabled, "Branch is enabled")
        val enabledEvent = eventQueryService.getLastEvent(branch, EventFactory.ENABLE_BRANCH)
        assertNotNull(enabledEvent, "Enabled event is there") { event ->
            val eventBranch: Branch? = event.getEntity(ProjectEntityType.BRANCH)
            assertNotNull(eventBranch, "Branch associated to the event") {
                assertEquals(branch.id, it.id)
            }
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun createBranch_denied() {
        // Project
        val project = doCreateProject()
        // Branch
        asUser().call { controller.newBranch(project.id, nameDescription().asState()) }
    }

    private fun checkBranchResource(branch: Branch, nameDescription: NameDescriptionState) {
        assertNotNull(branch, "Branch not null")
        assertNotNull(branch.id, "Branch ID not null")
        assertTrue(branch.id.isSet, "Branch ID set")
        assertEquals(nameDescription.name, branch.name, "Branch name")
        assertEquals(nameDescription.description, branch.description, "Branch description")
        assertEquals(nameDescription.isDisabled, branch.isDisabled, "Branch state")
    }

}
