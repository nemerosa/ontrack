package net.nemerosa.ontrack.extension.av.properties.yaml

import net.nemerosa.ontrack.common.BaseException

class YamlNoContentException(
    expression: String,
) : BaseException(
    "Cannot evaluate expression because there is no content: $expression"
)