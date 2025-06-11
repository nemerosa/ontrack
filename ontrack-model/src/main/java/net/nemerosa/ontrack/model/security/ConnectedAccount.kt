package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.preferences.Preferences
import net.nemerosa.ontrack.model.structure.Entity.Companion.isEntityDefined

@Deprecated("Will be removed in V5")
open class ConnectedAccount(
    val account: Account?,
    val preferences: Preferences,
) {

    @get:JsonProperty("logged")
    val isLogged: Boolean
        get() = account != null

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
