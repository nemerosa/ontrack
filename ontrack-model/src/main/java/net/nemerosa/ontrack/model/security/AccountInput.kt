package net.nemerosa.ontrack.model.security;

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Creation of a built-in account.
 */
data class AccountInput(
        @get:NotNull(message = "The account name is required.")
        @get:Pattern(regexp = "[a-zA-Z0-9@_.-]+", message = "The account name must contain only letters, digits, underscores, @, dashes and dots.")
        val name: String,
        @get:NotNull(message = "The account full name is required.")
        @get:Size(min = 1, max = 100, message = "The account full name must be between 1 and 100 long.")
        val fullName: String,
        @get:NotNull(message = "The account email is required.")
        @get:Size(min = 1, max = 200, message = "The account email must be between 1 and 200 long.")
        val email: String,
        val password: String?,
        /**
         * List of selected groups. It can be `null`, meaning that the list of groups
         * must be empty.
         */
        val groups: Collection<Int>?,
        val disabled: Boolean,
        val locked: Boolean,
)
