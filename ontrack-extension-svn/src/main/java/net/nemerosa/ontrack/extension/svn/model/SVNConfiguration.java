package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

@Data
public class SVNConfiguration implements UserPasswordConfiguration<SVNConfiguration> {

    private final String name;
    private final String url;
    private final String user;
    @SuppressWarnings("UnusedDeclaration")
    private final String password;
    private final String branchPattern;
    private final String tagPattern;
    private final String tagFilterPattern;
    private final String browserForPath;
    private final String browserForRevision;
    private final String browserForChange;
    private final int indexationInterval;
    private final long indexationStart;
    private final String issueServiceConfigurationIdentifier;

    public static Form form(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
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
                        Text.of("branchPattern")
                                .label("Branch pattern")
                                .length(250)
                                .optional()
                                .help("Regular expression applied to SVN paths in order to detect the branches. " +
                                        "It defaults to .*/branches/.*")
                )
                .with(
                        Text.of("tagPattern")
                                .label("Tag pattern")
                                .length(250)
                                .optional()
                                .help("Regular expression applied to SVN paths in order to detect the tags. " +
                                        "It defaults to .*/tags/.*")
                )
                .with(
                        Text.of("tagFilterPattern")
                                .label("Tag filter pattern")
                                .length(100)
                                .optional()
                                .help("Regular expression applied to tag names. Any tag whose name matches " +
                                        "will be excluded from the tags. By default, no tag is excluded.")
                )
                .with(
                        Text.of("browserForPath")
                                .label("Browsing URL for a path")
                                .length(400)
                                .optional()
                                .help("URL that defines how to browse to a path. The path is relative to the " +
                                        "repository root and must be parameterized as {path} in the URL.")
                )
                .with(
                        Text.of("browserForRevision")
                                .label("Browsing URL for a revision")
                                .length(400)
                                .optional()
                                .help("URL that defines how to browse to a revision. The revision must be " +
                                        "parameterized as {revision} in the URL.")
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
                        Int.of("indexationInterval")
                                .label("Indexation interval")
                                .min(0)
                                .max(60 * 24)
                                .value(0)
                                .help("Interval (in minutes) between each indexation of the Subversion repository. A " +
                                        "zero value indicates that no indexation must take place automatically and they " +
                                        "have to be triggered manually.")
                )
                .with(
                        Int.of("indexationStart")
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
                );
    }

    @Override
    public SVNConfiguration obfuscate() {
        return new SVNConfiguration(
                name,
                url,
                user,
                "",
                branchPattern,
                tagPattern,
                tagFilterPattern,
                browserForPath,
                browserForRevision,
                browserForChange,
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        );
    }

    public Form asForm(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return form(availableIssueServiceConfigurations)
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
                .fill("branchPattern", branchPattern)
                .fill("tagPattern", tagPattern)
                .fill("tagFilterPattern", tagFilterPattern)
                .fill("browserForPath", browserForPath)
                .fill("browserForRevision", browserForRevision)
                .fill("browserForChange", browserForChange)
                .fill("indexationInterval", indexationInterval)
                .fill("indexationStart", indexationStart)
                .fill("issueServiceConfigurationIdentifier", issueServiceConfigurationIdentifier)
                ;
    }

    @Override
    public SVNConfiguration withPassword(String password) {
        return new SVNConfiguration(
                name,
                url,
                user,
                password,
                branchPattern,
                tagPattern,
                tagFilterPattern,
                browserForPath,
                browserForRevision,
                browserForChange,
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        );
    }

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(name, name);
    }

    /**
     * Gets the absolute URL to a path relative to this repository.
     */
    public String getUrl(String path) {
        return StringUtils.stripEnd(url, "/")
                + "/"
                + StringUtils.stripStart(path, "/");
    }

    public String getRevisionBrowsingURL(long revision) {
        if (StringUtils.isNotBlank(browserForRevision)) {
            return browserForRevision.replace("{revision}", String.valueOf(revision));
        } else {
            return String.valueOf(revision);
        }
    }

    public String getPathBrowsingURL(String path) {
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("{path}", path);
        } else {
            return path;
        }
    }

    public String getFileChangeBrowsingURL(String path, long revision) {
        if (StringUtils.isNotBlank(browserForChange)) {
            return browserForChange.replace("{path}", path).replace("{revision}", String.valueOf(revision));
        } else {
            return path;
        }
    }

    @Override
    public SVNConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new SVNConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password,
                replacementFunction.apply(branchPattern),
                replacementFunction.apply(tagPattern),
                replacementFunction.apply(tagFilterPattern),
                replacementFunction.apply(browserForPath),
                replacementFunction.apply(browserForRevision),
                replacementFunction.apply(browserForChange),
                indexationInterval,
                indexationStart,
                issueServiceConfigurationIdentifier
        );
    }
}
