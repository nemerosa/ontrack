package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.config.AutoVersioningNotificationScope
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumAutoVersioningNotificationScope : AbstractGQLEnum<AutoVersioningNotificationScope>(
    AutoVersioningNotificationScope::class,
    AutoVersioningNotificationScope.values(),
    "Scope for notifications in auto versioning."
)
