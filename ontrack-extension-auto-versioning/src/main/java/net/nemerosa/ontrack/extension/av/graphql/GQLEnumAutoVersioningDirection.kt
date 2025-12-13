package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumAutoVersioningDirection : AbstractGQLEnum<AutoVersioningDirection>(
    AutoVersioningDirection::class,
    AutoVersioningDirection.values(),
    "List of directions when getting auto versioning information between two builds."
)
