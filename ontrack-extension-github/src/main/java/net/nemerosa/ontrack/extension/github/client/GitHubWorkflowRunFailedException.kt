package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.common.BaseException

class GitHubWorkflowRunFailedException(repository: String, runId: Long) : BaseException(
    "Workflow run $runId on repository $repository has failed."
)
