package net.nemerosa.ontrack.model.security

enum class SecurityRole(
        /**
         * Security role name
         */
        val roleName: String
) {

    ADMINISTRATOR("ROLE_ADMIN"),
    USER("ROLE_USER");

    val roleAbbreviatedName: String = roleName.substring(5)

}