package net.nemerosa.ontrack.common

/**
 * User exceptions are business exceptions, which can be tracked and will not
 * rollback a transaction.
 */
abstract class UserException(message: String) : Exception(message)
