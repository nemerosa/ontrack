package net.nemerosa.ontrack.extension.artifactory.configuration

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
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

    fun asForm(): Form {
        return form()
            .with(defaultNameField().readOnly().value(name))
            .fill("url", url)
            .fill("user", user)
            .fill("password", "")
    }

    override fun withPassword(password: String?): ArtifactoryConfiguration {
        return ArtifactoryConfiguration(
            name,
            url,
            user,
            password
        )
    }

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(name, name)

    companion object {
        @JvmStatic
        fun form(): Form {
            return Form.create()
                .with(defaultNameField())
                .url()
                .with(Text.of("user").label("User").length(16).optional())
                .with(Password.of("password").label("Password").length(40).optional())
        }
    }
}
