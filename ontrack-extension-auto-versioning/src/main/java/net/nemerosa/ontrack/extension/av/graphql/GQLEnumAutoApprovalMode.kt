package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumAutoApprovalMode : AbstractGQLEnum<AutoApprovalMode>(
    AutoApprovalMode::class,
    AutoApprovalMode.values(),
    "List of ways the auto approval is managed."
)
