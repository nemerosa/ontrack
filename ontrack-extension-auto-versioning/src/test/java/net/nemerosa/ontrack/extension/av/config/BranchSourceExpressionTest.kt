package net.nemerosa.ontrack.extension.av.config

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BranchSourceExpressionTest {

    @Test
    fun `ID only`() {
        val (id, config) = BranchSourceExpression.parseBranchSourceExpression("same")
        assertEquals("same", id)
        assertEquals(null, config)
    }

    @Test
    fun `ID and configuration`() {
        val (id, config) = BranchSourceExpression.parseBranchSourceExpression("same-release:2")
        assertEquals("same-release", id)
        assertEquals("2", config)
    }

}