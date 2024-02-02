package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.templating.TemplatingSource
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EntityQualifiedLongNameTemplatingSourceTest {

    private val source: TemplatingSource = EntityQualifiedLongNameTemplatingSource()

    @Test
    fun `Branch qualified long name`() {
        val branch = BranchFixtures.testBranch()
        val text = source.render(branch, emptyMap(), PlainEventRenderer.INSTANCE)
        assertEquals(
            "branch ${branch.project.name}/${branch.name}",
            text
        )
    }

}