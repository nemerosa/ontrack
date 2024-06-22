package net.nemerosa.ontrack.extension.notifications.model

import kotlin.reflect.KClass

interface NotificationSource<T : Any> {

    val id: String
    val displayName: String
    val dataType: KClass<T>

}