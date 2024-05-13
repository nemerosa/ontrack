package net.nemerosa.ontrack.extension.notifications.channels

/**
 * Used to annotate a notification channel for the documentation, indicating that
 * it does not use the provided template.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class NoTemplate
