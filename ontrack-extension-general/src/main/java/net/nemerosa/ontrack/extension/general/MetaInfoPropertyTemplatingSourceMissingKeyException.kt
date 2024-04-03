package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.templating.TemplatingException

class MetaInfoPropertyTemplatingSourceMissingKeyException(name: String) : TemplatingException(
    """No meta information key was found: $name"""
)
