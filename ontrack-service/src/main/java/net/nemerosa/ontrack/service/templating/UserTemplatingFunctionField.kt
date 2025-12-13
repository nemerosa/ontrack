package net.nemerosa.ontrack.service.templating

enum class UserTemplatingFunctionField {

    /**
     * Username
     */
    @Deprecated("Will be removed in V6. Use EMAIL instead. Replaced by the email.")
    NAME,

    /**
     * Display name
     */
    DISPLAY,

    /**
     * Email
     */
    EMAIL,

}