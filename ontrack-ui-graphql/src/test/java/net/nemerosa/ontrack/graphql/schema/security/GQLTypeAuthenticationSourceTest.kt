package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverterTest
import net.nemerosa.ontrack.model.security.AuthenticationSource
import org.junit.Test
import kotlin.test.assertEquals

class GQLTypeAuthenticationSourceTest {

    @Test
    fun introspection() {
        val type = GraphQLBeanConverter.asObjectType(AuthenticationSource::class, GQLTypeCache())
        assertEquals("AuthenticationSource", type.name)
        val fields = GraphQLBeanConverterTest.fieldToTypeNames(type.fieldDefinitions)
        assertEquals(
                mapOf(
                        "provider" to "String!",
                        "key" to "String!",
                        "name" to "String!",
                        "enabled" to "Boolean!",
                        "allowingPasswordChange" to "Boolean!",
                        "groupMappingSupported" to "Boolean!"
                ),
                fields
        )
    }

}