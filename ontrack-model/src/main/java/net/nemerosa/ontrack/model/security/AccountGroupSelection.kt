package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.support.Selectable

/**
 * Defines the selection of an [AccountGroup].
 */
class AccountGroupSelection(
    override val id: String,
    override val name: String,
    override val isSelected: Boolean
) : Selectable {

    constructor(group: AccountGroup, isSelected: Boolean) : this(
        id = group.id().toString(),
        name = group.name,
        isSelected = isSelected,
    )

}
