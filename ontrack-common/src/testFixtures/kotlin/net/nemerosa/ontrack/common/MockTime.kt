package net.nemerosa.ontrack.common

import java.time.LocalDateTime

object MockTime : TimeServer {

    var clock = Time.now()

    override val now: LocalDateTime get() = clock
}
