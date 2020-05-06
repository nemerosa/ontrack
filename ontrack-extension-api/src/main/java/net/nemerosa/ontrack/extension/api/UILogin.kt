package net.nemerosa.ontrack.extension.api

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.common.Document

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
        val name: String,

        /**
         * Description
         */
        val description: String,

        /**
         * Is there an image associated with this login?
         */
        val image: Boolean = false,

        /**
         * Optional loader for an image
         */
        @JsonIgnore
        val imageLoader: () -> Document? = { null }

)
