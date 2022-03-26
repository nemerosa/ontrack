package net.nemerosa.ontrack.extension.notifications.listener

import net.nemerosa.ontrack.model.events.Event

interface EventListeningService {

    fun onEvent(event: Event)

}