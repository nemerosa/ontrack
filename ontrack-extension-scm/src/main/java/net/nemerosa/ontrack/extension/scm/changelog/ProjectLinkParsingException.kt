package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.exceptions.InputException

class ProjectLinkParsingException(token: String) : InputException(
    """Project link cannot be parsed: $token"""
)
