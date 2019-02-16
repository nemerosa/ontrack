package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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
        val fields = type.fieldDefinitions.associate { it.name to it.type.name }
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
    fun `Composite type`() {
        val type = GraphQLBeanConverter.asObjectType(Account::class.java, cache)
        assertEquals("Account", type.name)
        val fields = type.fieldDefinitions.associate { it.name to it.type.name }
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
        val fields = type.fieldDefinitions.associate { it.name to it.type.name }
        assertEquals(
                mapOf(
                        "delegate" to "Account",
                        "account" to "Account"
                ),
                fields
        )
    }

}
