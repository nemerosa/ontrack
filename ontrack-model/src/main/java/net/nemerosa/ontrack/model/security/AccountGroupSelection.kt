package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.support.Selectable

/**
 * Defines the selection of an [AccountGroup].
 */
class AccountGroupSelection(
        private val id: Int,
        private val name: String,
        private val isSelected: Boolean
) : Selectable {

    constructor(group: AccountGroup, isSelected: Boolean) : this(
            group.id(), group.name, isSelected
    )

    override fun getId(): String = id.toString()

    override fun isSelected(): Boolean = isSelected

    override fun getName(): String = name
}
