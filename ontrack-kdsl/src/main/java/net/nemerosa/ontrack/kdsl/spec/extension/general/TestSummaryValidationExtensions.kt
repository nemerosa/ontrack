package net.nemerosa.ontrack.kdsl.spec.extension.general

import com.apollographql.apollo.api.Optional
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
            buildId = id.toInt(),
            description = Optional.presentIfNotNull(description),
            validation = validation,
            status = Optional.presentIfNotNull(status),
            passed = testSummary.passed,
            skipped = testSummary.skipped,
            failed = testSummary.failed,
        )
    ) {
        it?.validateBuildByIdWithTests?.payloadUserErrors?.convert()
    }
}

data class TestSummary(
    val passed: Int,
    val skipped: Int,
    val failed: Int,
)
