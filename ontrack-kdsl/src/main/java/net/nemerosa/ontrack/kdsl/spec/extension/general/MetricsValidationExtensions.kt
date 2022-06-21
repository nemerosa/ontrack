package net.nemerosa.ontrack.kdsl.spec.extension.general

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.ValidateWithMetricsMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.MetricsEntryInput
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Build

fun Build.validateWithMetrics(
    validation: String,
    description: String = "",
    status: String,
    metrics: Map<String, Double>,
) {
    graphqlConnector.mutate(
        ValidateWithMetricsMutation(
            id.toInt(),
            Input.fromNullable(description),
            validation,
            Input.fromNullable(status),
            metrics.map { (name, value) ->
                MetricsEntryInput.builder()
                    .name(name)
                    .value(value)
                    .build()
            }
        )
    ) {
        it?.validateBuildByIdWithMetrics()?.fragments()?.payloadUserErrors()?.convert()
    }
}

