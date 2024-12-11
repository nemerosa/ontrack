package net.nemerosa.ontrack.model.files

import net.nemerosa.ontrack.model.exceptions.InputException

class FileRefURIParsingException(uri: String) : InputException(
    """Cannot parse the file reference: $uri"""
)
