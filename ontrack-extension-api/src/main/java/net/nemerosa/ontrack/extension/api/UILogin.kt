package net.nemerosa.ontrack.extension.api

/**
 * Contribution to the login page
 */
data class UILogin(
        /**
         * Unique ID for this extension
         */
        val id: String,

        /**
         * Gets the link to go to
         */
        val link: String,

        /**
         * Gets the display name
         */
        val name: String
)