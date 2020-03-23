package net.nemerosa.ontrack.extension.jira

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.apache.commons.lang3.StringUtils
import java.util.function.Function
import java.util.regex.Pattern

open class JIRAConfiguration(
        private val name: String,
        val url: String,
        private val user: String?,
        private val password: String?
) : UserPasswordConfiguration<JIRAConfiguration>, IssueServiceConfiguration {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

    override fun obfuscate(): JIRAConfiguration {
        return JIRAConfiguration(
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

    override fun withPassword(password: String?): JIRAConfiguration {
        return JIRAConfiguration(
                name,
                url,
                user,
                password
        )
    }

    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(name, name)
    }

    @JsonIgnore
    override fun getServiceId(): String {
        return JIRAServiceExtension.SERVICE
    }

    fun isIssue(token: String): Boolean {
        return ISSUE_PATTERN.matcher(token).matches() && !isIssueExcluded(token)
    }

    private fun isIssueExcluded(@Suppress("UNUSED_PARAMETER") token: String): Boolean {
        return false
    }

    fun getIssueURL(key: String): String {
        val base = url
        return if (StringUtils.isNotBlank(base)) {
            String.format("%s/browse/%s", base, key)
        } else {
            key
        }
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): JIRAConfiguration {
        return JIRAConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(user) },
                password
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JIRAConfiguration

        if (name != other.name) return false
        if (url != other.url) return false
        if (user != other.user) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }

    companion object {

        private const val ISSUE_PATTERN_REGEX = "([A-Z]+-\\d+)"

        @JvmField
        val ISSUE_PATTERN: Pattern = Pattern.compile(ISSUE_PATTERN_REGEX)

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
