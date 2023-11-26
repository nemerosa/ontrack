package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.TokenScope
import org.springframework.stereotype.Component

@Component
class GQLEnumTokenScope : AbstractGQLEnum<TokenScope>(
    type = TokenScope::class,
    values = TokenScope.values(),
    description = "Class of tokens",
)