package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Representation of a user in Ontrack. Mostly used for traceability and storage.
 */
data class User(
        val name: String
) {

    @get:JsonIgnore
    val isAnonymous: Boolean = ANONYMOUS == name

    companion object {
        /**
         * Name for an anonymous user
         */
        const val ANONYMOUS = "anonymous"

        /**
         * Unique instance of the anonymous user
         */
        private val ANONYMOUS_USER = User(ANONYMOUS)

        /**
         * Anonymous user
         */
        fun anonymous(): User = ANONYMOUS_USER

        /**
         * Builder for a user
         */
        fun of(name: String): User = User(name)
    }

}