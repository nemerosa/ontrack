package net.nemerosa.ontrack.extension.oidc.settings

object OntrackOIDCProviderFixtures {

    fun testProvider(
            id: String,
            name: String = "Test"
    ) = OntrackOIDCProvider(
            id = id,
            name = name,
            description = "",
            issuerId = "",
            clientId = "",
            clientSecret = "",
            groupFilter = null
    )

}