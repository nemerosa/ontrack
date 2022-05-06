package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.common.BaseException

class ConfigParsingException(ex: Exception) : BaseException(
    ex, "Cannot parse ingestion configuration YAML."
)