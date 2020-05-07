package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.common.Document

object OntrackOIDCProviderFixtures {

    fun testProvider(
            id: String,
            name: String = "Test",
            description: String = ""
    ) = OntrackOIDCProvider(
            id = id,
            name = name,
            description = description,
            issuerId = "",
            clientId = "",
            clientSecret = "",
            groupFilter = null
    )

    fun image() = Document(
            "image/png",
            OntrackOIDCProviderFixtures::class.java
                    .getResourceAsStream("/image.png")
                    .readAllBytes()
    )

}