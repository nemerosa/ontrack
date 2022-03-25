package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.events.EventFactoryImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SlackNotificationEventRendererTest {

    private val eventFactory: EventFactory = EventFactoryImpl()
    private val htmlNotificationEventRenderer = SlackNotificationEventRenderer(
        OntrackConfigProperties().apply {
            url = "https://ontrack.nemerosa.net"
        }
    )


    @Test
    fun `New project`() {
        val project = project()
        val event = eventFactory.newProject(project)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """New project [PRJ](https://ontrack.nemerosa.net/#/project/1).""",
            text
        )
    }

    @Test
    fun `Deleted project`() {
        val project = project()
        val event = eventFactory.deleteProject(project)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """Project _PRJ_ has been deleted.""",
            text
        )
    }

    @Test
    fun `New branch`() {
        val branch = branch()
        val event = eventFactory.newBranch(branch)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """New branch [main](https://ontrack.nemerosa.net/#/branch/10) for project [PRJ](https://ontrack.nemerosa.net/#/project/1).""",
            text
        )
    }

    @Test
    fun `New promotion run`() {
        val promotionRun = promotionRun()
        val event = eventFactory.newPromotionRun(promotionRun)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """Build [1](https://ontrack.nemerosa.net/#/build/100) has been promoted to [PL](https://ontrack.nemerosa.net/#/promotionLevel/200) for branch [main](https://ontrack.nemerosa.net/#/branch/10) in [PRJ](https://ontrack.nemerosa.net/#/project/1).""",
            text
        )
    }

    @Test
    fun `Property change`() {
        val event = eventFactory.propertyChange(
            project(),
            TestSimplePropertyType(TestExtensionFeature())
        )
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """_Simple value_ property has changed for _project_ [PRJ](https://ontrack.nemerosa.net/#/project/1).""",
            text
        )
    }

    private fun project() = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))
    private fun branch(project: Project = project()) =
        Branch.of(project, NameDescription.nd("main", "")).withId(ID.of(10))

    private fun build(branch: Branch = branch()) = Build.of(
        branch,
        NameDescription.nd("1", ""),
        Signature.of("test")
    ).withId(ID.of(100))

    private fun promotionLevel(branch: Branch = branch()) = PromotionLevel.of(
        branch,
        NameDescription.nd("PL", ""),
    ).withId(ID.of(200))

    private fun promotionRun(branch: Branch = branch()) = PromotionRun.of(
        build(branch),
        promotionLevel(branch),
        Signature.of("test"),
        ""
    ).withId(ID.of(20000))

}