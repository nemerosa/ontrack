package net.nemerosa.ontrack.extension.github.ingestion.config.parser

import net.nemerosa.ontrack.common.BaseException

class ConfigVersionException(version: String) : BaseException(
    "Unsuported version for the ingestion configuration: $version."
)