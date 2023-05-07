package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class TFCNoConfigurationException(url: String) : NotFoundException(
    """Could not find a TFC configuration for URL $url."""
)