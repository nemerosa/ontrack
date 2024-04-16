package net.nemerosa.ontrack.common

import java.time.LocalDateTime

interface TimeServer {

    val now: LocalDateTime

}