package net.nemerosa.ontrack.model.security

/**
 * List of groups for an account, grouped per origin.
 */
data class AccountGroups(
    val assignedGroups: List<AccountGroup>,
    val mappedGroups: List<AccountGroup>,
    val idpGroups: List<String>,
)
