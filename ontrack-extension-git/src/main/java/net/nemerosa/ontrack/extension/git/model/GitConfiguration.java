package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.support.configurations.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GitConfiguration implements UserPasswordConfiguration<GitConfiguration> {

    /**
     * Name of this configuration
     */
    @Wither
    private final String name;

    /**
     * Remote path to the source repository
     */
    @Wither
    private final String remote;

    /**
     * Default branch
     */
    @Wither
    private final String branch;

    /**
     * User name
     */
    @Wither
    private final String user;

    /**
     * User password
     */
    @Wither
    private final String password;

    /**
     * Link to a commit, using {commit} as placeholder
     */
    @Wither
    private final String commitLink;

    /**
     * Link to a file at a given commit, using {commit} and {path} as placeholders
     */
    @Wither
    private final String fileAtCommitLink;

    /**
     * Indexation interval
     */
    @Wither
    private final int indexationInterval;

    /**
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    @Wither
    private final String issueServiceConfigurationIdentifier;

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(
                name,
                String.format("%s (%s)", name, remote)
        );
    }

    @Override
    public GitConfiguration obfuscate() {
        return this;
    }

    public static Form form(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return Form.create()
                .with(defaultNameField())
                .with(
                        Text.of("remote")
                                .label("Remote")
                                .help("Remote path to the source repository")
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
                        Text.of("commitLink")
                                .label("Commit link")
                                .length(250)
                                .optional()
                                .help("Link to a commit, using {commit} as placeholder")
                )
                .with(
                        Text.of("fileAtCommitLink")
                                .label("File at commit link")
                                .length(250)
                                .optional()
                                .help("Link to a file at a given commit, using {commit} and {path} as placeholders")
                )
                .with(
                        Int.of("indexationInterval")
                                .label("Indexation interval")
                                .min(0)
                                .max(60 * 24)
                                .value(0)
                                .help("Interval (in minutes) between each indexation of the Git repository. A " +
                                        "zero value indicates that no indexation must take place automatically and they " +
                                        "have to be triggered manually.")
                )
                .with(
                        Selection.of("issueServiceConfigurationIdentifier")
                                .label("Issue configuration")
                                .help("Select an issue service that is sued to associate tickets and issues to the source.")
                                .optional()
                                .items(availableIssueServiceConfigurations)
                );
    }

    public Form asForm(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return form(availableIssueServiceConfigurations)
                .with(defaultNameField().readOnly().value(name))
                .fill("remote", remote)
                .fill("user", user)
                .fill("password", "")
                .fill("commitLink", commitLink)
                .fill("fileAtCommitLink", fileAtCommitLink)
                .fill("indexationInterval", indexationInterval)
                .fill("issueServiceConfigurationIdentifier", issueServiceConfigurationIdentifier)
                ;
    }

    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(remote);
    }

    public static GitConfiguration empty() {
        return new GitConfiguration(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                ""
        );
    }

    public GitConfiguration merge(GitConfiguration configuration) {
        return new GitConfiguration(
                name,
                defaultIfBlank(configuration.remote, remote),
                defaultIfBlank(configuration.branch, branch),
                defaultIfBlank(configuration.user, user),
                defaultIfBlank(configuration.password, password),
                defaultIfBlank(configuration.commitLink, commitLink),
                defaultIfBlank(configuration.fileAtCommitLink, fileAtCommitLink),
                configuration.indexationInterval > 0 ? configuration.indexationInterval : indexationInterval,
                defaultIfBlank(configuration.issueServiceConfigurationIdentifier, issueServiceConfigurationIdentifier)
        );
    }
}
