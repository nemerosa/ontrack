package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID

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