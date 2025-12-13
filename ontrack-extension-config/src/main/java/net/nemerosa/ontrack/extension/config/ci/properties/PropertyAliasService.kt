package net.nemerosa.ontrack.extension.config.ci.properties

import net.nemerosa.ontrack.model.structure.PropertyAlias

interface PropertyAliasService {
    fun findPropertyAlias(type: String): PropertyAlias?
}