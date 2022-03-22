package net.nemerosa.ontrack.extension.notifications.dispatching

data class NotificationDispatchingResult(
    val potential: Int,
    val sent: Int,
    val ignored: Int,
) {

    operator fun plus(b: NotificationDispatchingResult) = NotificationDispatchingResult(
        potential = potential + b.potential,
        sent = sent + b.sent,
        ignored = ignored + b.ignored,
    )

    companion object {
        val ZERO = NotificationDispatchingResult(
            potential = 0,
            sent = 0,
            ignored = 0,
        )
    }
}
