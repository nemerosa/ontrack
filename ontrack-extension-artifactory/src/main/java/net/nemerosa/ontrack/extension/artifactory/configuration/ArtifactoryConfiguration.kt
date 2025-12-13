package net.nemerosa.ontrack.extension.artifactory.configuration

import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

open class ArtifactoryConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?
) : UserPasswordConfiguration<ArtifactoryConfiguration>(name, user, password) {

    override fun obfuscate(): ArtifactoryConfiguration {
        return ArtifactoryConfiguration(
            name,
            url,
            user,
            ""
        )
    }

    override fun withPassword(password: String?): ArtifactoryConfiguration {
        return ArtifactoryConfiguration(
            name,
            url,
            user,
            password
        )
    }

}
