package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import kotlin.test.*

class ProjectControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var controller: ProjectController

    @Autowired
    private lateinit var eventQueryService: EventQueryService

    @Test
    fun createProject() {
        asUser().with(ProjectCreation::class.java).execute {
            val nameDescription = nameDescription().asState()
            val resource = controller.newProject(nameDescription)
            checkProject(resource.data, nameDescription)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun createProject_denied() {
        asUser().call { controller.newProject(nameDescription().asState()) }
    }

    @Test
    fun disablingEnablingProject() {
        val project = doCreateProject()
        // Disables it
        val disabled = asUser().with(project, ProjectEdit::class.java).call { controller.disableProject(project.id) }
        assertTrue(disabled.data.isDisabled, "Project is disabled")
        val disabledEvent = eventQueryService.getLastEvent(project, EventFactory.DISABLE_PROJECT)
        assertNotNull(disabledEvent, "Disabled event is there") { event ->
            val eventProject: Project? = event.getEntity(ProjectEntityType.PROJECT)
            assertNotNull(eventProject, "Project associated to the event") {
                assertEquals(project.name, it.name)
            }
        }
        // Enables it
        val enabled = asUser().with(project, ProjectEdit::class.java).call { controller.enableProject(project.id) }
        assertFalse(enabled.data.isDisabled, "Project is enabled")
        val enabledEvent = eventQueryService.getLastEvent(project, EventFactory.ENABLE_PROJECT)
        assertNotNull(enabledEvent, "Enabled event is there") { event ->
            val eventProject: Project? = event.getEntity(ProjectEntityType.PROJECT)
            assertNotNull(eventProject, "Project associated to the event") {
                assertEquals(project.name, it.name)
            }
        }
    }

    @Test
    fun updateProject() {
        // Creates the project
        val initialNames = nameDescription().asState()
        val project = asUser().with(ProjectCreation::class.java).call { controller.newProject(initialNames) }.data
        val id = project.id
        // Edition
        asUser().with(id.value, ProjectEdit::class.java).execute {
            // Updates
            val nameDescription = nameDescription().asState()
            assertNotEquals(initialNames, nameDescription)
            var updated = controller.saveProject(id, nameDescription).data
            // Checks
            checkProject(updated, nameDescription)
            // Gets the project back
            updated = controller.getProject(id).data
            checkProject(updated, nameDescription)
        }
    }

    private fun checkProject(project: Project, nameDescription: NameDescriptionState) {
        assertNotNull(project, "Project not null")
        assertNotNull(project.id, "Project ID not null")
        assertTrue(project.id.isSet, "Project ID set")
        assertEquals(nameDescription.name, project.name, "Project name")
        assertEquals(nameDescription.description, project.description, "Project description")
        assertEquals(nameDescription.isDisabled, project.isDisabled, "Project state")
    }

}
