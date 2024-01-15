package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.support.ConnectionResultType
import org.springframework.stereotype.Component

@Component
class GQLEnumConnectionResultType : AbstractGQLEnum<ConnectionResultType>(
    type = ConnectionResultType::class,
    values = ConnectionResultType.values(),
    description = "Type of result returned when checking a configuration"
)
