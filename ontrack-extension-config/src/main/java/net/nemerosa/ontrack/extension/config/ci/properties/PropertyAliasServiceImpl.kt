package net.nemerosa.ontrack.extension.config.ci.properties

import org.springframework.stereotype.Component

@Component
class PropertyAliasServiceImpl(
    propertyAliases: List<PropertyAlias>,
) : PropertyAliasService {

    private val index = propertyAliases.associateBy { it.alias }

    override fun findPropertyAlias(type: String): PropertyAlias? = index[type]
}