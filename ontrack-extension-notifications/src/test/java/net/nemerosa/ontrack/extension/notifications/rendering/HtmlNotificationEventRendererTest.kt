package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.service.events.EventFactoryImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HtmlNotificationEventRendererTest {

    private val eventFactory: EventFactory = EventFactoryImpl()
    private val htmlNotificationEventRenderer = HtmlNotificationEventRenderer(
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
            """New project <a href="https://ontrack.nemerosa.net/#/project/1">PRJ</a>.""",
            text
        )
    }

    @Test
    fun `Deleted project`() {
        val project = project()
        val event = eventFactory.deleteProject(project)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """Project PRJ has been deleted.""",
            text
        )
    }

    @Test
    fun `New branch`() {
        val branch = branch()
        val event = eventFactory.newBranch(branch)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """New branch <a href="https://ontrack.nemerosa.net/#/branch/10">main</a> for project <a href="https://ontrack.nemerosa.net/#/project/1">PRJ</a>.""",
            text
        )
    }

    @Test
    fun `New promotion run`() {
        val promotionRun = promotionRun()
        val event = eventFactory.newPromotionRun(promotionRun)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """Build <a href="https://ontrack.nemerosa.net/#/build/100">1</a> has been promoted to <a href="https://ontrack.nemerosa.net/#/promotionLevel/200">PL</a> for branch <a href="https://ontrack.nemerosa.net/#/branch/10">main</a> in <a href="https://ontrack.nemerosa.net/#/project/1">PRJ</a>.""",
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
            """Simple value property has changed for project <a href="https://ontrack.nemerosa.net/#/project/1">PRJ</a>.""",
            text
        )
    }

    private fun project() = Project.of(nd("PRJ", "")).withId(ID.of(1))
    private fun branch(project: Project = project()) =
        Branch.of(project, nd("main", "")).withId(ID.of(10))

    private fun build(branch: Branch = branch()) = Build.of(
        branch,
        nd("1", ""),
        Signature.of("test")
    ).withId(ID.of(100))

    private fun promotionLevel(branch: Branch = branch()) = PromotionLevel.of(
        branch,
        nd("PL", ""),
    ).withId(ID.of(200))

    private fun promotionRun(branch: Branch = branch()) = PromotionRun.of(
        build(branch),
        promotionLevel(branch),
        Signature.of("test"),
        ""
    ).withId(ID.of(20000))

}