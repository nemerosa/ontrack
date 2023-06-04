package net.nemerosa.ontrack.graphql.schema

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class GQLTypeValidationRunStatusIDTest {

    @Test
    fun booleanFields() {
        val typeBuilder = GQLTypeValidationRunStatusID()
        val type = typeBuilder.createType(GQLTypeCache())
        // Root
        assertNotNull(type.getField("root"))
        // Passed
        assertNotNull(type.getField("passed"))
    }

}