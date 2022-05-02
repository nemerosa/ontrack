package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GQLInputEventSubscriptionFilterTest {

    @Test
    fun `Input type`() {
        val inputType = GQLInputEventSubscriptionFilter().createInputType(mutableSetOf())
        assertIs<GraphQLInputObjectType>(inputType) { inputObjectType ->
            assertIs<GraphQLTypeReference>(inputObjectType.getField("entity")?.type) {
                assertEquals("ProjectEntityIDInput", it.name)
            }
            assertNull(inputObjectType.getField("offset"), "Offset field is skipped")
            assertNull(inputObjectType.getField("size"), "Size field is skipped")
        }
    }

}