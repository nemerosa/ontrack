package net.nemerosa.ontrack.graphql.schema.links

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.links.BranchLinksDecorationLabel
import org.springframework.stereotype.Component

@Component
class GQLEnumBranchLinksDecorationLabel : AbstractGQLEnum<BranchLinksDecorationLabel>(
    BranchLinksDecorationLabel::class,
    BranchLinksDecorationLabel.values(),
    "Indicates how the label of a decoration must be displayed"
)
