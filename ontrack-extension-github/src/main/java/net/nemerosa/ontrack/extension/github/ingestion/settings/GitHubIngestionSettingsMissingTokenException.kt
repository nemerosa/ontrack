package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.common.BaseException

class GitHubIngestionSettingsMissingTokenException : BaseException(
    """The GitHub secret token for the ingestion of workflows has not been set."""
)
