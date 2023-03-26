package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.CredentialsConfiguration

class TFCConfiguration(
    override val name: String,
    val url: String,
    val token: String,
) : CredentialsConfiguration<TFCConfiguration> {

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