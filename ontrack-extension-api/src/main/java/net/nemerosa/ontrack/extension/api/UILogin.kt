package net.nemerosa.ontrack.extension.api

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.common.Document

/**
 * Contribution to the login page
 */
class UILogin(
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

) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is UILogin) return false

                if (id != other.id) return false
                if (link != other.link) return false
                if (name != other.name) return false
                if (description != other.description) return false
                if (image != other.image) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id.hashCode()
                result = 31 * result + link.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + description.hashCode()
                result = 31 * result + image.hashCode()
                return result
        }

        override fun toString(): String {
                return "UILogin(id='$id', link='$link', name='$name', description='$description', image=$image)"
        }


}