package net.nemerosa.ontrack.common

/**
 * User exceptions are business exceptions.
 */
abstract class UserException(message: String, exception: Exception? = null) : BaseException(exception, message)
