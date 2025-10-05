package net.nemerosa.ontrack.extension.config.ci.conditions

import com.fasterxml.jackson.databind.node.TextNode
import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BranchConditionTest {

    private val condition = BranchCondition()

    @Test
    fun `Matching branch`() {
        val ciEngine = mockk<CIEngine>()
        every { ciEngine.getBranchName(any()) } returns "release/1.0"
        assertTrue(
            condition.matches(
                ciEngine = ciEngine,
                config = TextNode("release.*"),
                env = emptyMap(),
            )
        )
    }

    @Test
    fun `Unmatching branch`() {
        val ciEngine = mockk<CIEngine>()
        every { ciEngine.getBranchName(any()) } returns "main"
        assertFalse(
            condition.matches(
                ciEngine = ciEngine,
                config = TextNode("release.*"),
                env = emptyMap(),
            )
        )
    }

}