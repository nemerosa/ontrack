package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class EventExtensionsTest {

    private val project = Project.of(nd("project", "")).withId(ID.of(1))
    private val branch = Branch.of(project, nd("main", "")).withId(ID.of(1))
    private val promotionLevel = PromotionLevel.of(branch, nd("GOLD", "")).withId(ID.of(1))
    private val build = Build.of(branch, nd("1", ""), Signature.of("test")).withId(ID.of(1))
    private val run = PromotionRun.of(build, promotionLevel, Signature.of("test"), "").withId(ID.of(1))

    val event = Event.of(EventFactory.NEW_PROMOTION_RUN)
        .with(project)
        .with(branch)
        .with(promotionLevel)
        .with(build)
        .with(run)
        .build()

    @Test
    fun `Matching events on one keyword`() {
        assertTrue(event.matchesKeywords("main"))
        assertTrue(event.matchesKeywords("GOLD"))
        assertFalse(event.matchesKeywords("release"))
        assertFalse(event.matchesKeywords("SILVER"))
    }

    @Test
    fun `Matching events on two keyword`() {
        assertTrue(event.matchesKeywords("GOLD main"))
        assertFalse(event.matchesKeywords("GOLD release"))
    }

    @Test
    fun `Matching events on null keywords`() {
        assertTrue(event.matchesKeywords(null))
    }

    @Test
    fun `Matching events on empty keywords`() {
        assertTrue(event.matchesKeywords(""))
    }

}