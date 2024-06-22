package net.nemerosa.ontrack.extension.notifications.model

import net.nemerosa.ontrack.json.asJson

fun <T : Any> NotificationSource<T>.createData(data: T) =
    NotificationSourceData(
        id = this.id,
        data = data.asJson()
    )
