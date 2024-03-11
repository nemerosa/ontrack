package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Result of a connection test.
 */
// #542 Using open as a workaround
open class ConnectionResult(
    @APIDescription("Type of result")
    val type: ConnectionResultType,
    @APIDescription("Result message")
    val message: String,
) {

    companion object {

        @JvmStatic
        fun ok(): ConnectionResult {
            return ConnectionResult(ConnectionResultType.OK, "")
        }

        @JvmStatic
        fun error(message: String): ConnectionResult {
            return ConnectionResult(ConnectionResultType.ERROR, message)
        }

        fun error(any: Throwable) = ConnectionResult(
            type = ConnectionResultType.ERROR,
            message = any.message ?: any::class.java.name
        )
    }

}
