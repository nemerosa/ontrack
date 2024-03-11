package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.multiStrings
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.apache.commons.lang3.StringUtils

/**
 * JIRA configuration.
 *
 * @param name Name for this configuration
 * @param url URL to the JIRA server
 * @param user Username for the connection to the JIRA server
 * @param password Password or token for the connection to the JIRA server
 * @param include List of regular expressions for the accepted JIRA projects (default = empty = all of them)
 * @param exclude List of regular expressions for the JIRA projects to not accept (default = empty = none of them)
 */
open class JIRAConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?,
    val include: List<String> = emptyList(),
    val exclude: List<String> = emptyList(),
) : UserPasswordConfiguration<JIRAConfiguration>(name, user, password), IssueServiceConfiguration {

    private val includeRegexes: List<Regex> by lazy {
        include.map { it.toRegex() }
    }

    private val excludeRegexes: List<Regex> by lazy {
        exclude.map { it.toRegex() }
    }

    override fun obfuscate(): JIRAConfiguration {
        return JIRAConfiguration(
            name = name,
            url = url,
            user = user,
            password = "",
            include = include,
            exclude = exclude,
        )
    }

    fun asForm(): Form {
        return form()
            .with(defaultNameField().readOnly().value(name))
            .fill("url", url)
            .fill("user", user)
            .fill("password", "")
            .fill("include", include)
            .fill("exclude", exclude)
    }

    override fun withPassword(password: String?): JIRAConfiguration {
        return JIRAConfiguration(
            name = name,
            url = url,
            user = user,
            password = password,
            include = include,
            exclude = exclude,
        )
    }

    override val descriptor: ConfigurationDescriptor get() = ConfigurationDescriptor(name, name)

    override val serviceId: String = JIRAServiceExtension.SERVICE

    fun getIssueURL(key: String): String {
        val base = url
        return if (StringUtils.isNotBlank(base)) {
            String.format("%s/browse/%s", base, key)
        } else {
            key
        }
    }

    fun isValidIssueKey(token: String): Boolean {
        if (token.isNotBlank() && token.matches(ISSUE_PATTERN_REGEX)) {
            // Included?
            if (include.isNotEmpty() && includeRegexes.none { token.matches(it) }) {
                return false
            }
            // Excluded?
            if (exclude.isNotEmpty() && excludeRegexes.any { token.matches(it) }) {
                return false
            }
            // OK
            return true
        } else {
            return false
        }
    }

    companion object {

        val ISSUE_PATTERN_REGEX = "(?:[^A-Z0-9]|^)([A-Z][A-Z0-9]+-\\d+)(?:[^0-9]|\$)".toRegex()

        @JvmStatic
        fun form(): Form {
            return Form.create()
                .with(defaultNameField())
                .url()
                .with(Text.of("user").label("User").length(16).optional())
                .with(Password.of("password").label("Password").length(40).optional())
                .multiStrings(JIRAConfiguration::include, null)
                .multiStrings(JIRAConfiguration::exclude, null)
        }
    }
}
