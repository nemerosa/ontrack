package net.nemerosa.ontrack.model.support

/**
 * Password change request.
 */
data class PasswordChange(val oldPassword: String, val newPassword: String)
