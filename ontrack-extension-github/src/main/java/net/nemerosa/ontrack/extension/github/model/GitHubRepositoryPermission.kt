package net.nemerosa.ontrack.extension.github.model

enum class GitHubRepositoryPermission {

    /**
     * Can read, clone, and push to this repository. Can also manage issues, pull requests, and repository settings, including adding collaborators
     */
    ADMIN,

    /**
     * Can read, clone, and push to this repository. They can also manage issues, pull requests, and some repository settings
     */
    MAINTAIN,

    /**
     * Can read, clone, and push to this repository. Can also manage issues and pull requests
     */
    WRITE,

    /**
     * Can read and clone this repository. Can also manage issues and pull requests
     */
    TRIAGE,

    /**
     * Can read and clone this repository. Can also open and comment on issues and pull requests
     */
    READ

}
