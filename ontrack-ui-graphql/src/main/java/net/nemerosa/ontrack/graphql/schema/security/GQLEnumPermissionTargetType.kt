package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.security.PermissionTargetType
import org.springframework.stereotype.Component

@Component
class GQLEnumPermissionTargetType : AbstractGQLEnum<PermissionTargetType>(
    type = PermissionTargetType::class,
    values = PermissionTargetType.entries.toTypedArray(),
    description = "Target for a permission assignment"
)
