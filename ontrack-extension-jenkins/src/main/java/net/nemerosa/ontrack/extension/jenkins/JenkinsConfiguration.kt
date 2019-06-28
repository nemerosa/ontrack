package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import java.util.function.Function

open class JenkinsConfiguration(
        private val name: String,
        val url: String,
        private val user: String?,
        private val password: String?
) : UserPasswordConfiguration<JenkinsConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

    override fun obfuscate(): JenkinsConfiguration {
        return JenkinsConfiguration(
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

    override fun withPassword(password: String?): JenkinsConfiguration {
        return JenkinsConfiguration(
                name,
                url,
                user,
                password
        )
    }

    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(name, name)
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): JenkinsConfiguration {
        return JenkinsConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(it) },
                password
        )
    }

    companion object {

        fun form(): Form {
            return Form.create()
                    .with(defaultNameField())
                    .url()
                    .with(Text.of("user").label("User").length(16).optional())
                    .with(Password.of("password").label("Password").length(40).optional())
        }
    }
}
