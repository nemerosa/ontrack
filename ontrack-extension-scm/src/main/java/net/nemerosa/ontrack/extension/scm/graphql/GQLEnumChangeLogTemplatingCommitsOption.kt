package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.changelog.ChangeLogTemplatingCommitsOption
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumChangeLogTemplatingCommitsOption : AbstractGQLEnum<ChangeLogTemplatingCommitsOption>(
    type = ChangeLogTemplatingCommitsOption::class,
    values = ChangeLogTemplatingCommitsOption.values(),
    description = "Option for the commit display in the templating"
)