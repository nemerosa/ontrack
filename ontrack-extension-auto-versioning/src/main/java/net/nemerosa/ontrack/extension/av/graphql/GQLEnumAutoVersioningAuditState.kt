package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumAutoVersioningAuditState : AbstractGQLEnum<AutoVersioningAuditState>(
    AutoVersioningAuditState::class,
    AutoVersioningAuditState.values(),
    "State an auto versioning audit entry can be in"
)
