package net.nemerosa.ontrack.extension.jenkins

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
                other.user == user &&
                other.password == password

    override fun obfuscate(): JenkinsConfiguration {
        return JenkinsConfiguration(
            name,
            url,
            user,
            ""
        )
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

}
