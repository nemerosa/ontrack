package net.nemerosa.ontrack.kdsl.spec.extension.general

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.ValidateWithTestSummaryMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Build

fun Build.validateWithTestSummary(
    validation: String,
    description: String = "",
    status: String? = null,
    testSummary: TestSummary,
) {
    graphqlConnector.mutate(
        ValidateWithTestSummaryMutation(
            id.toInt(),
            Input.fromNullable(description),
            validation,
            Input.fromNullable(status),
            testSummary.passed,
            testSummary.skipped,
            testSummary.failed,
        )
    ) {
        it?.validateBuildByIdWithTests()?.fragments()?.payloadUserErrors()?.convert()
    }
}

data class TestSummary(
    val passed: Int,
    val skipped: Int,
    val failed: Int,
)
