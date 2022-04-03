package net.nemerosa.ontrack.extension.notifications.channels

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumNotificationResultType: AbstractGQLEnum<NotificationResultType>(
    NotificationResultType::class,
    NotificationResultType.values(),
    "Type of result for a notification"
)