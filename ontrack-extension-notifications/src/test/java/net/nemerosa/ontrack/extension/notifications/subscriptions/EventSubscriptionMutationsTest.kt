package net.nemerosa.ontrack.extension.notifications.subscriptions

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class EventSubscriptionMutationsTest {

    @Test
    fun `EventSubscriptionEntityInput input type`() {
        val type = GraphQLBeanConverter.asInputType(EventSubscriptionEntityInput::class, mutableSetOf())
        assertIs<GraphQLInputObjectType>(type) { input ->
            assertNotNull(input.getField("type")) { typeField ->
                assertIs<GraphQLNonNull>(typeField.type) { typeNotNullType ->
                    assertIs<GraphQLTypeReference>(typeNotNullType.wrappedType) { typeType ->
                        assertEquals("ProjectEntityType", typeType.name)
                    }
                }
            }

        }
    }

}