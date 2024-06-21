package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.exceptions.InputException

abstract class TemplatingException(message: String) : InputException(message)

class TemplatingNoContextFoundException(contextKey: String) : TemplatingException(
    """Templating key not found in context: $contextKey"""
)

class TemplatingConfiguredLiteralException(contextKey: String) : TemplatingException(
    """Templating key refers to a literal and should not be configured nor having a field: $contextKey"""
)

class TemplatingExpressionFormatException(expression: String) : TemplatingException(
    """Templating expression is malformed: $expression"""
)

class TemplatingMissingFunctionException : TemplatingException(
    """Templating function is expected after #"""
)

class TemplatingFunctionNotFoundException(function: String) : TemplatingException(
    """Templating function not found: $function"""
)

class TemplatingEntityNameHavingConfigException : TemplatingException(
    """Templating refers to a project entity name. No config is expected in this case."""
)

class TemplatingNoFieldSourceException(field: String) : TemplatingException(
    """Templating field is not bound to any source: $field"""
)

class TemplatingMultipleFieldSourcesException(field: String) : TemplatingException(
    """Templating field is bound to more than 1 source: $field"""
)

class TemplatingMissingConfigParam(key: String) : TemplatingException(
    """Missing required templating config param: $key"""
)

class TemplatingMisconfiguredConfigParamException(key: String, text: String) : TemplatingException(
    """Misconfigured templating config param: $key. $text"""
)

class TemplatingConfigFormatException(expression: String) : TemplatingException(
    """Misconfigured configuration: $expression"""
)

class TemplatingFilterNotFoundException(filter: String) : TemplatingException(
    """Templating filter not found: $filter"""
)

class TemplatingRenderableFieldNotFoundException(field: String) : TemplatingException(
    """Templating field not managed: $field"""
)

class TemplatingRenderableFieldRequiredException : TemplatingException(
    """Templating field is required"""
)

class TemplatingGeneralException(message: String) : TemplatingException(message)
