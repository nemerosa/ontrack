package net.nemerosa.ontrack.model.support

/**
 * Defines an item that can be selected.
 */
interface Selectable {
    val isSelected: Boolean

    val id: String

    val name: String
}
