package net.nemerosa.ontrack.extension.av.config

import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GQLTypeAutoVersioningConfigTest {

    @Test
    fun `Auto approval mode enum`() {
        val cache = GQLTypeCache()
        GraphQLBeanConverter.asObjectType(AutoVersioningConfig::class, cache)

        assertNotNull(cache["AutoVersioningSourceConfig"]) { type ->
            assertIs<GraphQLTypeReference>(type.fieldDefinitions.find {
                it.name == "autoApprovalMode"
            }?.type) { fieldType ->
                assertEquals("AutoApprovalMode", fieldType.name)
            }
        }
    }

}