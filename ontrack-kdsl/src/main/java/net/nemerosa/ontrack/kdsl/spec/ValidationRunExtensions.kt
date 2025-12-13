package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.ValidationRunFragment

/**
 * Creates a [ValidationRun] from a GraphQL [ValidationRunFragment].
 */
fun ValidationRunFragment.toValidationRun(connected: Connected) = ValidationRun(
    connector = connected.connector,
    id = id.toUInt(),
    description = description,
    data = data?.let { data ->
        ValidationRunData(
            type = data.descriptor!!.id!!,
            data = data.data.asJson(),
        )
    },
    statuses = validationRunStatuses.map {
        ValidationRunStatus(
            id = it.statusID!!.id,
            description = it.description ?: "",
            annotatedDescription = it.annotatedDescription ?: "",
        )
    }
)
