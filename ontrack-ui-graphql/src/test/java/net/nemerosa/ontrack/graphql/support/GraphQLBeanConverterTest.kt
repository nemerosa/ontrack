package net.nemerosa.ontrack.graphql.support

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNamedType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GraphQLBeanConverterTest {

    private lateinit var cache: GQLTypeCache

    @Before
    fun before() {
        cache = GQLTypeCache()
    }

    @Test
    fun `Simple type`() {
        val type = GraphQLBeanConverter.asObjectType(Person::class.java, cache)
        assertEquals("Person", type.name)
        val fields = fieldToTypeNames(type.fieldDefinitions)
        assertEquals(
                mapOf(
                        "name" to "String",
                        "address" to "String",
                        "age" to "Int",
                        "developer" to "Boolean"
                ),
                fields
        )
    }

    @Test
    fun `Description from annotation`() {
        val type = GraphQLBeanConverter.asObjectType(Person::class.java, cache)
        assertEquals("Person", type.name)
        assertNotNull(type.fieldDefinitions.find { it.name == "name" }) {
            assertEquals("Full name", it.description)
        }
        assertNotNull(type.fieldDefinitions.find { it.name == "address" }) {
            assertEquals("Full postal address", it.description)
        }
    }

    @Test
    fun `Composite type`() {
        val type = GraphQLBeanConverter.asObjectType(Account::class.java, cache)
        assertEquals("Account", type.name)
        val fields = fieldToTypeNames(type.fieldDefinitions)
        assertEquals(
                mapOf(
                        "username" to "String",
                        "password" to "String",
                        "identity" to "Person"
                ),
                fields
        )
    }

    @Test
    fun `Composite type with three levels`() {
        val type = GraphQLBeanConverter.asObjectType(OnBehalf::class.java, cache)
        assertEquals("OnBehalf", type.name)
        val fields = fieldToTypeNames(type.fieldDefinitions)
        assertEquals(
                mapOf(
                        "delegate" to "Account",
                        "account" to "Account"
                ),
                fields
        )
    }

    private fun fieldToTypeNames(fields: List<GraphQLFieldDefinition>): Map<String, String> =
            fields.associate {
                it.name to (it.type as GraphQLNamedType).name
            }

}
