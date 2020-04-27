package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID

/**
 * Defines a mapping between a provided group (by a LDAP system for example)
 * and an actual Ontrack [group][AccountGroup].
 *
 * @property id Unique ID of this mapping
 * @property type Type of mapping (maps to [AuthenticationSource.id])
 * @property name Name of the provided group
 * @property group ID of the [AccountGroup]
 */
data class AccountGroupMapping(
        override val id: ID,
        val type: String,
        val name: String,
        val group: AccountGroup
) : Entity {

    fun asForm(groups: List<AccountGroup>): Form = form(groups)
            .fill("name", name)
            .fill("group", group.id)

    companion object {
        @JvmStatic
        fun form(groups: List<AccountGroup>): Form = create()
                .name()
                .with(
                        Selection.of("group")
                                .label("Group")
                                .items(groups)
                )
    }

}