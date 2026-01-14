package net.nemerosa.ontrack.service.templating

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TemplatingExpressionTest {

    @Test
    fun `Multiple entries with = signs`() {
        val expression =
            TemplatingExpression.parse(
                "promotionRun.semanticChangelog?issues=true&emojis=true&sections=ci=Delivery&sections=chore=Other"
            )
        assertNotNull(expression) {
            assertEquals("promotionRun", it.contextKey)
            assertEquals("semanticChangelog", it.field)
            assertEquals("issues=true&emojis=true&sections=ci=Delivery&sections=chore=Other", it.configQuery)
            assertEquals(null, it.filter)
        }
    }

    @Test
    fun `Full query`() {
        val expression =
            TemplatingExpression.parse(
                "promotionRun.semanticChangelog?issues=true&emojis=true&exclude=ci"
            )
        assertNotNull(expression) {
            assertEquals("promotionRun", it.contextKey)
            assertEquals("semanticChangelog", it.field)
            assertEquals("issues=true&emojis=true&exclude=ci", it.configQuery)
            assertEquals(null, it.filter)
        }

    }

    @Test
    fun `Simple entry`() {
        val expression = TemplatingExpression.parse("build")
        assertNotNull(expression) {
            assertEquals("build", it.contextKey)
            assertEquals(null, it.field)
            assertEquals(null, it.configQuery)
            assertEquals(null, it.filter)
        }
    }

    @Test
    fun `Simple entry with filter`() {
        val expression = TemplatingExpression.parse("build|lowercase")
        assertNotNull(expression) {
            assertEquals("build", it.contextKey)
            assertEquals(null, it.field)
            assertEquals(null, it.configQuery)
            assertEquals("lowercase", it.filter)
        }
    }

    @Test
    fun `Simple entry with filter and query`() {
        val expression = TemplatingExpression.parse("build?issues=true|lowercase")
        assertNotNull(expression) {
            assertEquals("build", it.contextKey)
            assertEquals(null, it.field)
            assertEquals("issues=true", it.configQuery)
            assertEquals("lowercase", it.filter)
        }
    }

    @Test
    fun `Full query with filter`() {
        val expression =
            TemplatingExpression.parse(
                "promotionRun.semanticChangelog?issues=true&emojis=true&sections=ci=Delivery&sections=chore=Other|lowercase"
            )
        assertNotNull(expression) {
            assertEquals("promotionRun", it.contextKey)
            assertEquals("semanticChangelog", it.field)
            assertEquals("issues=true&emojis=true&sections=ci=Delivery&sections=chore=Other", it.configQuery)
            assertEquals("lowercase", it.filter)
        }
    }
}