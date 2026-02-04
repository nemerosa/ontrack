package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.service.SCMPullRequestStatus
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSCMPullRequestStatus : AbstractGQLEnum<SCMPullRequestStatus>(
    type = SCMPullRequestStatus::class,
    values = SCMPullRequestStatus.entries.toTypedArray(),
    description = "Status of a pull request"
)