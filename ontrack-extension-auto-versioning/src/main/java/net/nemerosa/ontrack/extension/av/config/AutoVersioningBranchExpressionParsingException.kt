package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.exceptions.InputException

class AutoVersioningBranchExpressionParsingException(expression: String) : InputException(
    """Cannot parse auto versioning expression: $expression."""
)
