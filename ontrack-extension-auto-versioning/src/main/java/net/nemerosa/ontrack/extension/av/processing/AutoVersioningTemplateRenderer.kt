package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.model.events.EventRenderer

interface AutoVersioningTemplateRenderer {

    fun render(template: String, renderer: EventRenderer): String

}