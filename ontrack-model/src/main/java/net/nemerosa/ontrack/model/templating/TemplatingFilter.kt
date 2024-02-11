package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer

interface TemplatingFilter {

    val id: String

    fun apply(text: String, renderer: EventRenderer): String

}