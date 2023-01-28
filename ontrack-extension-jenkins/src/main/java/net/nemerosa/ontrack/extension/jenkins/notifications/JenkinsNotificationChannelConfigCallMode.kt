package net.nemerosa.ontrack.extension.jenkins.notifications

/**
 * Defines how the notification is managed.
 */
enum class JenkinsNotificationChannelConfigCallMode {

    /**
     * In this mode, Ontrack calls the job and returns immediately.
     */
    ASYNC,

    /**
     * In this mode, Ontrack calls the job and waits for its completion.
     */
    SYNC,

}