package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.ValidationStampFragment


/**
 * Creates a [ValidationStamp] from a GraphQL [ValidationStampFragment].
 */
fun ValidationStampFragment.toValidationStamp(connected: Connected) = ValidationStamp(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
    dataType = dataType()?.descriptor()?.id(),
    dataTypeConfig = dataType()?.config()?.asJson(),
)
