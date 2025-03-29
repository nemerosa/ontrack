package net.nemerosa.ontrack.model

import java.util.*

data class Ack(
    val success: Boolean,
) {

    fun and(ack: Ack): Ack {
        return validate(success && ack.success)
    }

    fun or(ack: Ack): Ack {
        return validate(success || ack.success)
    }

    companion object {
        val OK: Ack = Ack(true)
        val NOK: Ack = Ack(false)

        fun validate(test: Boolean): Ack {
            return if (test) OK else NOK
        }

        fun validate(optional: Optional<*>?): Ack {
            return if (optional != null) validate(optional.isPresent) else NOK
        }

        fun one(count: Int): Ack {
            return validate(count == 1)
        }
    }
}
