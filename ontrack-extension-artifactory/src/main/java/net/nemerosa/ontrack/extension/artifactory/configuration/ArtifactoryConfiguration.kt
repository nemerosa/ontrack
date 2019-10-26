package net.nemerosa.ontrack.extension.artifactory.configuration

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.util.function.Function

open class ArtifactoryConfiguration(
        private val name: String,
        val url: String,
        private val user: String?,
        private val password: String?
) : UserPasswordConfiguration<ArtifactoryConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

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

    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(name, name)
    }


    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): ArtifactoryConfiguration {
        return ArtifactoryConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(user) },
                password
        )
    }

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
