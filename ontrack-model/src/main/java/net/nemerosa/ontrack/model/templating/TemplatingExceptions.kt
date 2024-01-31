package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.exceptions.InputException

class TemplatingNoContextFoundException(contextKey: String) : InputException(
    """Templating key not found in context: $contextKey"""
)

class TemplatingConfiguredLiteralException(contextKey: String) : InputException(
    """Templating key refers to a literal and should not be configured nor having a field: $contextKey"""
)

class TemplatingExpressionFormatException(expression: String) : InputException(
    """Templating expression is malformed: $expression"""
)

class TemplatingEntityNameHavingConfigException : InputException(
    """Templating refers to a project entity name. No config is expected in this case."""
)

class TemplatingNoFieldSourceException(field: String) : InputException(
    """Templating field is not bound to any source: $field"""
)

class TemplatingMultipleFieldSourcesException(field: String) : InputException(
    """Templating field is bound to more than 1 source: $field"""
)

class TemplatingMissingConfigParam(key: String) : InputException(
    """Missing required templating config param: $key"""
)

class TemplatingConfigFormatException(expression: String) : InputException(
    """Misconfigured configuration: $expression"""
)

class TemplatingFilterNotFoundException(filter: String) : InputException(
    """Templating filter not found: $filter"""
)
