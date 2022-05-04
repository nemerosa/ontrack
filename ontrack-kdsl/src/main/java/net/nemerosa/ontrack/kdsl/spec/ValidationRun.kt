package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType

/**
 * Representation of a validation run.
 *
 * @property connector Ontrack connector
 * @property id Validation run ID
 * @property name Validation run name
 * @property description Validation run description
 */
class ValidationRun(
    connector: Connector,
    id: UInt,
    val description: String?,
    val data: ValidationRunData?,
    val statuses: List<ValidationRunStatus>,
) : ProjectEntity(connector, ProjectEntityType.VALIDATION_RUN, id) {
    val lastStatus: ValidationRunStatus get() = statuses.first()
}
