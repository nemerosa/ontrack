package net.nemerosa.ontrack.extension.github.model

/**
 * Defines the way a client must authenticate to GitHub.
 */
enum class GitHubAuthenticationType {
    
    ANONYMOUS,
    PASSWORD,
    USER_TOKEN,
    TOKEN,
    APP

}