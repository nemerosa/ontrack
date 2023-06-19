package net.nemerosa.ontrack.extension.hook

import graphql.schema.GraphQLNamedOutputType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HookGraphQLTest {

    @Test
    fun `Checking the HookResponse GraphQL type`() {
        val type = GraphQLBeanConverter.asObjectType(HookResponse::class, GQLTypeCache())
        assertNotNull(type.fieldDefinitions.find { it.name == "info" }) { info ->
            assertIs<GraphQLNamedOutputType>(info.type) {
                assertEquals("JSON", it.name)
            }
        }
    }

}
