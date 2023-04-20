package net.nemerosa.ontrack.model.support

import java.net.URI

class Action(
    val id: String,
    val name: String,
    val type: ActionType,
    val uri: String,
    /**
     * Group this action must be put into.
     */
    val group: String? = null,
) {

    fun withUri(uri: String): Action = Action(id, name, type, uri, group)
    fun withGroup(group: String): Action = Action(id, name, type, uri, group)

    companion object {

        @JvmStatic
        fun of(id: String, name: String, uri: String, vararg parameters: Any): Action {
            return Action(id, name, ActionType.LINK, String.format(uri, *parameters))
        }

        @JvmStatic
        fun form(id: String, name: String, formUri: URI): Action {
            return Action(id, name, ActionType.FORM, formUri.toString())
        }
    }

}