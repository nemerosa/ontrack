package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.exceptions.InputException

class SCMRefURIParsingException(uri: String) : InputException(
    """Cannot parse the SCM URI: $uri"""
)
