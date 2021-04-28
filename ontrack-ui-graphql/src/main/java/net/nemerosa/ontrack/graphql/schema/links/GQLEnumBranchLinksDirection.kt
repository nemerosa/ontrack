package net.nemerosa.ontrack.graphql.schema.links

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import org.springframework.stereotype.Component

@Component
class GQLEnumBranchLinksDirection : AbstractGQLEnum<BranchLinksDirection>(
    BranchLinksDirection::class,
    BranchLinksDirection.values(),
    "Direction for dependencies"
)
