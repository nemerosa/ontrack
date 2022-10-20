package net.nemerosa.ontrack.extension.av.properties.yaml

import net.nemerosa.ontrack.common.BaseException

class YamlEvaluationException(
    expression: String,
    exception: Exception,
) : BaseException(
    exception,
    "Cannot evaluate expression because of an error: $expression"
)