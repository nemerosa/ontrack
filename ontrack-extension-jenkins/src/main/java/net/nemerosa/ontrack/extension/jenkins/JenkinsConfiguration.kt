package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

open class JenkinsConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?
) : UserPasswordConfiguration<JenkinsConfiguration>(name, user, password) {

    override fun equals(other: Any?): Boolean =
        other is JenkinsConfiguration &&
                other.name == name &&
                other.url == url &&
                other.name == name &&
                other.password == password

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

    override val descriptor = ConfigurationDescriptor(name, name)

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
