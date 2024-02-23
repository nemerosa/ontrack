package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.exceptions.InputException

class DependencyLinkParsingException(token: String) : InputException(
    """Dependency link cannot be parsed: $token"""
)
