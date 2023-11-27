package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.preferences.Preferences
import net.nemerosa.ontrack.model.structure.Entity.Companion.isEntityDefined
import net.nemerosa.ontrack.model.support.Action

open class ConnectedAccount(
    val account: Account?,
    val preferences: Preferences,
    private val actions: MutableList<Action> = mutableListOf(),
    val nextUIProperties: NextUIProperties = NextUIProperties(enabled = false),
) {

    val isLogged: Boolean
        get() = account != null

    fun add(action: Action): ConnectedAccount {
        actions.add(action)
        return this
    }

    fun filterActions() = ConnectedAccount(
        account = account,
        preferences = preferences,
        actions = actions.filter { it.enabled }.toMutableList(),
        nextUIProperties = nextUIProperties,
    )

    @Suppress("unused")
    fun getActions(): List<Action> {
        return actions
    }

    companion object {

        @JvmStatic
        fun none(): ConnectedAccount = ConnectedAccount(null, Preferences())

        @JvmStatic
        fun of(account: Account?, preferences: Preferences): ConnectedAccount {
            isEntityDefined(account, "Account must be defined")
            return ConnectedAccount(account, preferences)
        }
    }
}
