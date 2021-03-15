package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverterTest.Companion.typeName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValidationRunMutationsTest {

    @Test
    fun `Run info must be a field in the validation run input`() {
        val fields = GraphQLBeanConverter.asInputFields(CreateValidationRunByIdInput::class)
        val runInfo = fields.find { it.name == "runInfo" }
        assertNotNull(runInfo, "`runInfo` field is available") {
            assertEquals("RunInfoInput", typeName(it.type))
        }
    }

}