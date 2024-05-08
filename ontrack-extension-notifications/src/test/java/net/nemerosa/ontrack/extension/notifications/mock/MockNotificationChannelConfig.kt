package net.nemerosa.ontrack.extension.notifications.mock

/**
 * Configuration for the [MockNotificationChannel] channel.
 *
 * @property target Pseudo-address or channel to target
 * @property data Optional data to pass along to the output
 */
data class MockNotificationChannelConfig(
    val target: String,
    val data: String? = null,
)
