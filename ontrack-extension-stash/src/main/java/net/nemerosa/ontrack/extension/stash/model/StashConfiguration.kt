package net.nemerosa.ontrack.extension.stash.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.apache.commons.lang3.StringUtils
import java.lang.String.format

/**
 * @property name Name of this configuration
 * @property url Bitbucket URL
 * @property user User name
 * @property password User password
 * @property autoMergeToken Token used for approving pull requests for the auto merge operations
 */
// TODO #532 Workaround
open class StashConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?,
    @APILabel("Auto merge user")
    @APIDescription("Slug of the user approving pull requests for the auto merge operations")
    val autoMergeUser: String?,
    @APILabel("Auto merge token")
    @APIDescription("Token used for approving pull requests for the auto merge operations")
    val autoMergeToken: String?,
) : UserPasswordConfiguration<StashConfiguration>(name, user, password) {

    /**
     * Checks if this configuration denotes any Bitbucket Cloud instance
     */
    @Deprecated("Specific Bitbucket Cloud configuration must be used. Will be removed in V5.")
    val isCloud: Boolean
        @JsonIgnore
        get() = StringUtils.contains(url, "bitbucket.org")

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor(
            name,
            format("%s (%s)", name, url)
        )

    override fun obfuscate() = StashConfiguration(
        name = name,
        url = url,
        user = user,
        password = "",
        autoMergeUser = autoMergeUser,
        autoMergeToken = "",
    )

    override fun withPassword(password: String?): StashConfiguration {
        return StashConfiguration(
            name = name,
            url = url,
            user = user,
            password = password,
            autoMergeUser = autoMergeUser,
            autoMergeToken = autoMergeToken,
        )
    }

    fun asForm(): Form {
        return form()
            .with(defaultNameField().readOnly().value(name))
            .fill("url", url)
            .fill("user", user)
            .fill("password", "")
            .fill("autoMergeUser", autoMergeUser)
            .fill("autoMergeToken", "")
    }

    companion object {

        fun form(): Form {
            return Form.create()
                .with(defaultNameField())
                .with(
                    Text.of("url")
                        .label("URL")
                        .help("URL to the Bitbucket instance (https://bitbucket.org for example)")
                )
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
                .textField(StashConfiguration::autoMergeUser, null)
                .passwordField(StashConfiguration::autoMergeToken)
        }
    }
}
