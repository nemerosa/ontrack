package net.nemerosa.ontrack.extension.stash.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPassword
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.apache.commons.lang3.StringUtils
import java.lang.String.format
import java.util.*
import java.util.function.Function

/**
 * @property name Name of this configuration
 * @property url BitBucket URL
 * @property user User name
 * @property password User password
 */
class StashConfiguration(
        private val name: String,
        val url: String,
        private val user: String,
        private val password: String
) : UserPasswordConfiguration<StashConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

    /**
     * Checks if this configuration denotes any BitBucket Cloud instance
     */
    val isCloud: Boolean
        @JsonIgnore
        get() = StringUtils.contains(url, "bitbucket.org")

    @JsonIgnore
    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(
                name,
                format("%s (%s)", name, url)
        )
    }

    override fun obfuscate(): StashConfiguration {
        return withPassword("")
    }

    override fun withPassword(password: String): StashConfiguration {
        return StashConfiguration(
                name,
                url,
                user,
                password
        )
    }

    fun asForm(): Form {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): StashConfiguration {
        return StashConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password
        )
    }

    @JsonIgnore
    override fun getCredentials(): Optional<UserPassword> {
        return if (StringUtils.isNotBlank(user)) {
            Optional.of(
                    UserPassword(
                            user,
                            password
                    )
            )
        } else {
            Optional.empty()
        }
    }

    companion object {

        fun form(): Form {
            return Form.create()
                    .with(defaultNameField())
                    .with(
                            Text.of("url")
                                    .label("URL")
                                    .help("URL to the BitBucket instance (https://bitbucket.org for example)"))
                    .with(
                            Text.of("user")
                                    .label("User")
                                    .length(16)
                                    .optional()
                    )
                    .with(
                            Password.of("password")
                                    .label("Password")
                                    .length(40)
                                    .optional()
                    )
        }
    }
}
