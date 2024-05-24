package net.nemerosa.ontrack.model.events

fun EventRenderer.renderWithSpace(
    vararg items: String
) = items.joinToString(
    separator = renderSpace(),
)
