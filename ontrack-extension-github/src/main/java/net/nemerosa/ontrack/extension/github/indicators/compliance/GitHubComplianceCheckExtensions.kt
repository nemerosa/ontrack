package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorType

fun <T, C> GitHubComplianceCheck<T, C>.toConfigurableIndicatorType() = ConfigurableIndicatorType(
    category = category,
    id = id,
    name = name,
    attributes = attributes,
    valueType = valueType,
    valueConfig = valueConfig,
    computing = computing,
)
