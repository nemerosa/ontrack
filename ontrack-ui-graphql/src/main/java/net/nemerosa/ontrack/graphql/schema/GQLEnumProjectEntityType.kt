package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLEnumProjectEntityType : AbstractGQLEnum<ProjectEntityType>(
    type = ProjectEntityType::class,
    values = ProjectEntityType.values(),
    description = "Project entity type"
)
