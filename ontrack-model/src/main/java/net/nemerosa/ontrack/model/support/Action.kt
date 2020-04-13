package net.nemerosa.ontrack.model.support

import java.net.URI

class Action(
        val id: String,
        val name: String,
        val type: ActionType,
        val uri: String
) {

    fun withUri(uri: String): Action {
        return of(id, name, uri)
    }

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