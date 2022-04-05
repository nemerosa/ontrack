package net.nemerosa.ontrack.extension.casc.expressions

import net.nemerosa.ontrack.model.exceptions.InputException

class CascExpressionMissingNameException : InputException(
    """Casc expression is missing a name."""
)

class CascExpressionUnknownException(name: String) : InputException(
    """No Casc expression for name = $name."""
)
