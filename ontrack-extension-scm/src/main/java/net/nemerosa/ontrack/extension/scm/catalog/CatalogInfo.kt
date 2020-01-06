package net.nemerosa.ontrack.extension.scm.catalog

import java.time.LocalDateTime

class CatalogInfo<T>(
        val collector: CatalogInfoContributor<T>,
        val data: T?,
        val error: String?,
        val timestamp: LocalDateTime
)
