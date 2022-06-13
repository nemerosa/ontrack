package net.nemerosa.ontrack.extension.av.config

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.graphql.SetAutoVersioningConfigInput
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class AutoVersioningMutationsTest {

    @Test
    fun `SetAutoVersioningConfigInput input type`() {
        val dictionary = mutableSetOf<GraphQLType>()
        GraphQLBeanConverter.asInputType(SetAutoVersioningConfigInput::class, dictionary)
        val input = dictionary.find {
            it is GraphQLInputObjectType && it.name == "AutoVersioningSourceConfigInput"
        }
        assertNotNull(input, "AutoVersioningSourceConfigInput input type created")
    }

    @Test
    fun `Auto approval mode as enum`() {
        val dictionary = mutableSetOf<GraphQLType>()
        GraphQLBeanConverter.asInputType(SetAutoVersioningConfigInput::class, dictionary)

        assertNotNull(dictionary.find {
            it is GraphQLInputObjectType && it.name == "AutoVersioningSourceConfigInput"
        } as? GraphQLInputObjectType?) { input ->
            assertIs<GraphQLTypeReference>(input.getField("autoApprovalMode")?.type) { type ->
                assertEquals("AutoApprovalMode", type.name)
            }
        }
    }

}