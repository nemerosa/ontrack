package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.CredentialsConfiguration

// TODO #532 Using `open` as a workaround
open class TFCConfiguration(
    @APIDescription("Unique name for the configuration")
    @APILabel("Name")
    override val name: String,
    @APIDescription("API root URL")
    @APILabel("URL")
    val url: String,
    @APIDescription("User token to access the API")
    @APILabel("Token")
    val token: String,
) : CredentialsConfiguration<TFCConfiguration> {

    @APIIgnore
    override val descriptor = ConfigurationDescriptor(
        name,
        "$name ($url)"
    )

    override fun obfuscate() = TFCConfiguration(
        name = name,
        url = url,
        token = ""
    )

    override fun injectCredentials(oldConfig: TFCConfiguration) = TFCConfiguration(
        name = name,
        url = url,
        token = if (token.isBlank()) {
            oldConfig.token
        } else {
            token
        }
    )

    override fun encrypt(crypting: (plain: String?) -> String?) = TFCConfiguration(
        name = name,
        url = url,
        token = crypting(token) ?: ""
    )

    override fun decrypt(decrypting: (encrypted: String?) -> String?) = TFCConfiguration(
        name = name,
        url = url,
        token = decrypting(token) ?: ""
    )
}