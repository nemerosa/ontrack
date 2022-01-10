package net.nemerosa.ontrack.model.preferences

import net.nemerosa.ontrack.model.security.Account

/**
 * Access to the preferences of users.
 */
interface PreferencesService {

    /**
     * Gets the preferences of the [account].
     *
     * @param account Account to get the preferences for
     * @return Account's preferences.
     */
    fun getPreferences(account: Account): Preferences

    /**
     * Sets the preferences of the [account].
     *
     * @param account Account to set the preferences for
     * @param preferences Account's preferences.
     */
    fun setPreferences(account: Account, preferences: Preferences)

}