package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.displayName

fun Event.matchesFilter(filter: String?) =
    if (filter != null) {
        val tokens = filter.split(" ").map { it.trim().lowercase() }.toSet()
        tokens.all { matchesFilterWord(it) }
    } else {
        true
    }

private fun Event.matchesFilterWord(filter: String) =
    entities.values.any { entity -> entity.displayName.lowercase() == filter }
            || values.values.any { it.value.lowercase() == filter }
