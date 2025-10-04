package net.nemerosa.ontrack.extension.config.ci.properties

interface PropertyAliasService {
    fun findPropertyAlias(type: String): PropertyAlias?
}