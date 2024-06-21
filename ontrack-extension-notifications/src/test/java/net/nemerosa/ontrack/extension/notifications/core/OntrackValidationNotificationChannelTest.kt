package net.nemerosa.ontrack.extension.notifications.core

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OntrackValidationNotificationChannelTest {

    private lateinit var channel: OntrackValidationNotificationChannel
    private lateinit var structureService: StructureService
    private lateinit var eventTemplatingService: EventTemplatingService

    @BeforeEach
    fun before() {
        structureService = mockk()

        eventTemplatingService = mockk()
        every {
            eventTemplatingService.renderEvent(any(), any(), any())
        } answers {
            it.invocation.args[2] as? String? ?: error("No template provided")
        }

        channel = OntrackValidationNotificationChannel(
            eventTemplatingService = eventTemplatingService,
            structureService = structureService,
            securityService = MockSecurityService(),
            runInfoService = mockk(),
        )
    }

    @Test
    fun `Getting the project from the event and not found`() {
        val event = mockk<Event>()
        every { event.getEntity<Project>(ProjectEntityType.PROJECT) } throws RuntimeException("Project not found")
        assertFailsWith<RuntimeException> {
            channel.getTargetProject(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the project from the event and found`() {
        val event = mockk<Event>()
        val project = ProjectFixtures.testProject()
        every { event.getEntity<Project>(ProjectEntityType.PROJECT) } returns project
        assertEquals(
            project,
            channel.getTargetProject(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        )
    }

    @Test
    fun `Getting the project from its name and not found`() {
        val event = mockk<Event>()

        every {
            structureService.findProjectByName("test")
        } returns Optional.empty()

        assertFailsWith<ProjectNotFoundException> {
            channel.getTargetProject(
                config = OntrackValidationNotificationChannelConfig(
                    project = "test",
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the project from its name and found`() {
        val event = mockk<Event>()
        val project = ProjectFixtures.testProject()

        every {
            structureService.findProjectByName("test")
        } returns Optional.of(project)

        assertEquals(
            project,
            channel.getTargetProject(
                config = OntrackValidationNotificationChannelConfig(
                    project = "test",
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        )
    }

    @Test
    fun `Getting the branch from the event and not found`() {
        val event = mockk<Event>()
        every { event.getEntity<Branch>(ProjectEntityType.BRANCH) } throws RuntimeException("Branch not found")
        assertFailsWith<RuntimeException> {
            channel.getTargetBranch(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the branch from the event and found`() {
        val event = mockk<Event>()
        val branch = BranchFixtures.testBranch()
        every { event.getEntity<Branch>(ProjectEntityType.BRANCH) } returns branch
        assertEquals(
            branch,
            channel.getTargetBranch(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        )
    }

    @Test
    fun `Getting the branch from its name and not found`() {
        val event = mockk<Event>()
        val project = ProjectFixtures.testProject()

        every {
            structureService.findProjectByName(project.name)
        } returns Optional.of(project)

        every {
            structureService.findBranchByName(project.name, "main")
        } returns Optional.empty()

        assertFailsWith<BranchNotFoundException> {
            channel.getTargetBranch(
                config = OntrackValidationNotificationChannelConfig(
                    project = project.name,
                    branch = "main",
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the branch from its name and found`() {
        val event = mockk<Event>()
        val branch = BranchFixtures.testBranch(name = "main")

        every {
            structureService.findProjectByName(branch.project.name)
        } returns Optional.of(branch.project)

        every {
            structureService.findBranchByName(branch.project.name, "main")
        } returns Optional.of(branch)

        assertEquals(
            branch,
            channel.getTargetBranch(
                config = OntrackValidationNotificationChannelConfig(
                    project = branch.project.name,
                    branch = "main",
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        )
    }

    @Test
    fun `Getting the build from the event and not found`() {
        val event = mockk<Event>()
        every { event.getEntity<Build>(ProjectEntityType.BUILD) } throws RuntimeException("Build not found")
        assertFailsWith<RuntimeException> {
            channel.getTargetBuild(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the build from the event and found`() {
        val event = mockk<Event>()
        val build = BuildFixtures.testBuild()
        every { event.getEntity<Build>(ProjectEntityType.BUILD) } returns build
        assertEquals(
            build,
            channel.getTargetBuild(
                config = OntrackValidationNotificationChannelConfig(validation = "na"),
                event = event,
                context = emptyMap(),
            )
        )
    }

    @Test
    fun `Getting the build from its name and not found`() {
        val event = mockk<Event>()
        val branch = BranchFixtures.testBranch()

        every {
            structureService.findProjectByName(branch.project.name)
        } returns Optional.of(branch.project)

        every {
            structureService.findBranchByName(branch.project.name, branch.name)
        } returns Optional.of(branch)

        every {
            structureService.findBuildByName(branch.project.name, branch.name, "1.0")
        } returns Optional.empty()

        assertFailsWith<BuildNotFoundException> {
            channel.getTargetBuild(
                config = OntrackValidationNotificationChannelConfig(
                    project = branch.project.name,
                    branch = branch.name,
                    build = "1.0",
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        }
    }

    @Test
    fun `Getting the build from its name and found`() {
        val event = mockk<Event>()
        val build = BuildFixtures.testBuild()

        every {
            structureService.findProjectByName(build.branch.project.name)
        } returns Optional.of(build.branch.project)

        every {
            structureService.findBranchByName(build.branch.project.name, build.branch.name)
        } returns Optional.of(build.branch)

        every {
            structureService.findBuildByName(build.branch.project.name, build.branch.name, build.name)
        } returns Optional.of(build)

        assertEquals(
            build,
            channel.getTargetBuild(
                config = OntrackValidationNotificationChannelConfig(
                    project = build.branch.project.name,
                    branch = build.branch.name,
                    build = build.name,
                    validation = "na"
                ),
                event = event,
                context = emptyMap(),
            )
        )
    }

}