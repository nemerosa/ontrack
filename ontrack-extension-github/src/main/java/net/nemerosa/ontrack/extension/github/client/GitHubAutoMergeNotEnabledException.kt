package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.common.BaseException

class GitHubAutoMergeNotEnabledException(
    repository: String,
) : BaseException(
    """Auto merge of PRs seems not to be enabled in repository $repository."""
)
