package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

/**
 * Association of a [User] and a timestamp.
 */
data class Signature(
        val time: LocalDateTime,
        val user: User
) {
    fun withTime(dateTime: LocalDateTime?): Signature = Signature(dateTime ?: Time.now(), user)

    companion object {

        /**
         * Builder from a user name, for current time
         */
        @JvmStatic
        fun of(name: String): Signature = of(Time.now(), name)

        /**
         * Builder from a user name and a given time
         */
        @JvmStatic
        fun of(dateTime: LocalDateTime, name: String) = Signature(
                dateTime,
                User.of(name)
        )

        /**
         * Anonymous signature
         */
        fun anonymous(): Signature {
            return Signature(
                    Time.now(),
                    User.anonymous()
            )
        }

        /**
         * Anonymous signature
         */
        @Deprecated(
                message = "Use `anonymous` instead",
                replaceWith = ReplaceWith("anonymous()")
        )
        @JvmStatic
        fun none() = anonymous()

    }
}