package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.jupiter.api.Test
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
            checkProject(resource.body!!, nameDescription)
        }
    }

    @Test
    fun createProject_denied() {
        assertFailsWith<AccessDeniedException> {
            asUser().call { controller.newProject(nameDescription().asState()) }
        }
    }

    @Test
    fun disablingEnablingProject() {
        val project = doCreateProject()
        // Disables it
        val disabled = asUser().withProjectFunction(project, ProjectEdit::class.java).call { controller.disableProject(project.id) }.body!!
        assertTrue(disabled.isDisabled, "Project is disabled")
        val disabledEvent = asUser { eventQueryService.getLastEvent(project, EventFactory.DISABLE_PROJECT) }
        assertNotNull(disabledEvent, "Disabled event is there") { event ->
            val eventProject: Project = event.getEntity(ProjectEntityType.PROJECT)
            assertNotNull(eventProject, "Project associated to the event") {
                assertEquals(project.name, it.name)
            }
        }
        // Enables it
        val enabled = asUser().withProjectFunction(project, ProjectEdit::class.java).call { controller.enableProject(project.id) }.body!!
        assertFalse(enabled.isDisabled, "Project is enabled")
        val enabledEvent = asUser { eventQueryService.getLastEvent(project, EventFactory.ENABLE_PROJECT) }
        assertNotNull(enabledEvent, "Enabled event is there") { event ->
            val eventProject: Project = event.getEntity(ProjectEntityType.PROJECT)
            assertNotNull(eventProject, "Project associated to the event") {
                assertEquals(project.name, it.name)
            }
        }
    }

    @Test
    fun updateProject() {
        // Creates the project
        val initialNames = nameDescription().asState()
        val project = asUser().with(ProjectCreation::class.java).call { controller.newProject(initialNames) }.body!!
        val id = project.id
        // Edition
        asUser().withProjectFunction(project, ProjectEdit::class.java).execute {
            // Updates
            val nameDescription = nameDescription().asState()
            assertNotEquals(initialNames, nameDescription)
            var updated = controller.saveProject(id, nameDescription).body!!
            // Checks
            checkProject(updated, nameDescription)
            // Gets the project back
            updated = controller.getProject(id).body!!
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
