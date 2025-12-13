package net.nemerosa.ontrack.extension.github.notifications

/**
 * Defines how the notification is managed.
 */
enum class GitHubWorkflowNotificationChannelConfigCallMode {

    /**
     * In this mode, Yontrack calls the workflow and returns immediately.
     */
    ASYNC,

    /**
     * In this mode, Yontrack calls the workflow and waits for its completion.
     */
    SYNC,

}