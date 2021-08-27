package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttributeType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumConfigurableIndicatorAttributeType :
    AbstractGQLEnum<ConfigurableIndicatorAttributeType>(
        ConfigurableIndicatorAttributeType::class,
        ConfigurableIndicatorAttributeType.values(),
        "List of supported attribute types for configurable indicators"
    )
