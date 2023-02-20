package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSCMChangeLogFileChangeType : AbstractGQLEnum<SCMChangeLogFileChangeType>(
    type = SCMChangeLogFileChangeType::class,
    values = SCMChangeLogFileChangeType.values(),
    description = "Type of change on a file."
)
