package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration
import org.apache.commons.lang3.StringUtils
import java.util.function.Function

// TODO #532 Workaround
open class SVNConfiguration(
        private val name: String,
        val url: String,
        private val user: String?,
        private val password: String?,
        val tagFilterPattern: String?,
        val browserForPath: String?,
        val browserForRevision: String?,
        val browserForChange: String?,
        val indexationInterval: Int,
        val indexationStart: Long,
        val issueServiceConfigurationIdentifier: String?
) : UserPasswordConfiguration<SVNConfiguration> {

    override fun getName(): String = name

    override fun getUser(): String? = user

    override fun getPassword(): String? = password

    fun withUser(user: String?) =
            SVNConfiguration(
                    name,
                    url,
                    user,
                    password,
                    tagFilterPattern,
                    browserForPath,
                    browserForRevision,
                    browserForChange,
                    indexationInterval,
                    indexationStart,
                    issueServiceConfigurationIdentifier
            )

    fun withIndexationInterval(indexationInterval: Int) =
            SVNConfiguration(
                    name,
                    url,
                    user,
                    password,
                    tagFilterPattern,
                    browserForPath,
                    browserForRevision,
                    browserForChange,
                    indexationInterval,
                    indexationStart,
                    issueServiceConfigurationIdentifier
            )

    fun withIssueServiceConfigurationIdentifier(issueServiceConfigurationIdentifier: String?) =
            SVNConfiguration(
                    name,
                    url,
                    user,
                    password,
                    tagFilterPattern,
                    browserForPath,
                    browserForRevision,
                    browserForChange,
                    indexationInterval,
                    indexationStart,
                    issueServiceConfigurationIdentifier
            )

    override fun obfuscate(): SVNConfiguration {
        return SVNConfiguration(
                name,
                url,
                user,
                "",
                tagFilterPattern,
                browserForPath,
                browserForRevision,
                browserForChange,
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        )
    }

    fun asForm(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
        return form(availableIssueServiceConfigurations)
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
                .fill("tagFilterPattern", tagFilterPattern)
                .fill("browserForPath", browserForPath)
                .fill("browserForRevision", browserForRevision)
                .fill("browserForChange", browserForChange)
                .fill("indexationInterval", indexationInterval)
                .fill("indexationStart", indexationStart)
                .fill("issueServiceConfigurationIdentifier", issueServiceConfigurationIdentifier)
    }

    override fun withPassword(password: String?): SVNConfiguration {
        return SVNConfiguration(
                name,
                url,
                user,
                password,
                tagFilterPattern,
                browserForPath,
                browserForRevision,
                browserForChange,
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        )
    }

    override fun getDescriptor(): ConfigurationDescriptor {
        return ConfigurationDescriptor(name, name)
    }

    /**
     * Gets the absolute URL to a path relative to this repository.
     */
    fun getUrl(path: String): String {
        return (StringUtils.stripEnd(url, "/")
                + "/"
                + StringUtils.stripStart(path, "/"))
    }

    fun getRevisionBrowsingURL(revision: Long): String {
        return if (browserForRevision != null) {
            browserForRevision.replace("{revision}", revision.toString())
        } else {
            revision.toString()
        }
    }

    fun getPathBrowsingURL(path: String): String {
        return if (browserForPath != null) {
            browserForPath.replace("{path}", path)
        } else {
            path
        }
    }

    fun getFileChangeBrowsingURL(path: String, revision: Long): String {
        return if (browserForChange != null) {
            browserForChange.replace("{path}", path).replace("{revision}", revision.toString())
        } else {
            path
        }
    }

    override fun clone(targetConfigurationName: String, replacementFunction: Function<String, String>): SVNConfiguration {
        return SVNConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                user?.let { replacementFunction.apply(it) },
                password,
                tagFilterPattern?.let { replacementFunction.apply(it) },
                browserForPath?.let { replacementFunction.apply(it) },
                browserForRevision?.let { replacementFunction.apply(it) },
                browserForChange?.let { replacementFunction.apply(it) },
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        )
    }

    companion object {
        @JvmStatic
        fun of(name: String, url: String): SVNConfiguration {
            return SVNConfiguration(
                    name,
                    url, // indexation
                    null        // issue service
                    , null, // user, password
                    "", // tag filter pattern
                    "", "", "", // browser URL
                    0, 1L, null
            )
        }

        fun form(availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>): Form {
            return Form.create()
                    .with(defaultNameField())
                    .with(
                            // Note that the URL property cannot be implemented through a URL field
                            // since some SVN repository URL could use the svn: protocol or other.
                            Text.of("url")
                                    .label("URL")
                                    .help("URL to the root of a SVN repository")
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
                    .with(
                            Text.of("tagFilterPattern")
                                    .label("Tag filter pattern")
                                    .length(100)
                                    .optional()
                                    .help("Regular expression applied to tag names. Any tag whose name matches " + "will be excluded from the tags. By default, no tag is excluded.")
                    )
                    .with(
                            Text.of("browserForPath")
                                    .label("Browsing URL for a path")
                                    .length(400)
                                    .optional()
                                    .help("URL that defines how to browse to a path. The path is relative to the " + "repository root and must be parameterized as {path} in the URL.")
                    )
                    .with(
                            Text.of("browserForRevision")
                                    .label("Browsing URL for a revision")
                                    .length(400)
                                    .optional()
                                    .help("URL that defines how to browse to a revision. The revision must be " + "parameterized as {revision} in the URL.")
                    )
                    .with(
                            Text.of("browserForChange")
                                    .label("Browsing URL for a change")
                                    .length(400)
                                    .optional()
                                    .help("URL that defines how to browse to the changes of a path at a given revision. " +
                                            "The revision must be parameterized as {revision} in the URL and the path " +
                                            "as {path}.")
                    )
                    .with(
                            net.nemerosa.ontrack.model.form.Int.of("indexationInterval")
                                    .label("Indexation interval")
                                    .min(0)
                                    .max(60 * 24)
                                    .value(0)
                                    .help("Interval (in minutes) between each indexation of the Subversion repository. A " +
                                            "zero value indicates that no indexation must take place automatically and they " +
                                            "have to be triggered manually.")
                    )
                    .with(
                            net.nemerosa.ontrack.model.form.Int.of("indexationStart")
                                    .label("Indexation start")
                                    .min(1)
                                    .value(1)
                                    .help("Revision to start the indexation from.")
                    )
                    .with(
                            Selection.of("issueServiceConfigurationIdentifier")
                                    .label("Issue configuration")
                                    .help("Select an issue service that is sued to associate tickets and issues to the source.")
                                    .optional()
                                    .items(availableIssueServiceConfigurations)
                    )
        }
    }
}
