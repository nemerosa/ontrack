package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.changelog.PromotionChangeLogTemplatingServiceConfig

class PromotionRunChangeLogTemplatingSourceConfig(
    empty: String = "",
    dependencies: List<String> = emptyList(),
    title: Boolean = false,
    allQualifiers: Boolean = false,
    defaultQualifierFallback: Boolean = false,
    acrossBranches: Boolean = true
) : PromotionChangeLogTemplatingServiceConfig(
    empty,
    dependencies,
    title,
    allQualifiers,
    defaultQualifierFallback,
    acrossBranches
)