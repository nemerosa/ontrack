package net.nemerosa.ontrack.extension.tfc.client

interface TFCClient {

    /**
     * Gets the list of organizations
     */
    val organizations: List<TFCOrganization>

    /**
     * Gets the list of variables for a workspace
     */
    fun getWorkspaceVariables(workspaceId: String): List<TFCVariable>

}