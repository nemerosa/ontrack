package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("Status of a pull request")
enum class SCMPullRequestStatus {
    @APIDescription("PR status is unknown")
    UNKNOWN,
    @APIDescription("PR is open")
    OPEN,
    @APIDescription("PR is merged")
    MERGED,
    @APIDescription("PR is declined")
    DECLINED,
}