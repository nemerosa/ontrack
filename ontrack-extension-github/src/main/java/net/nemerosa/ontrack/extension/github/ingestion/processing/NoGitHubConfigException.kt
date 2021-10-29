package net.nemerosa.ontrack.extension.github.ingestion.processing

import net.nemerosa.ontrack.common.BaseException

class NoGitHubConfigException : BaseException(
    "No GitHub Workflow ingestion can be performed because no GitHub configuration has been registered in Ontrack."
)