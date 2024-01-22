package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class ConfigurationServiceTypeNotFoundException(type: String) : NotFoundException(
    """Configuration type not found: $type"""
)
