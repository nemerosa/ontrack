package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPassword;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

/**
 * Git configuration based on direct definition.
 */
@Data
@AllArgsConstructor
public class BasicGitConfiguration implements UserPasswordConfiguration<BasicGitConfiguration> {

    public static final String TYPE = "basic";

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

    public static BasicGitConfiguration empty() {
        return new BasicGitConfiguration(
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

    @Override
    @JsonIgnore
    public Optional<UserPassword> getCredentials() {
        return StringUtils.isNotBlank(user) ?
                Optional.of(new UserPassword(user, password)) :
                Optional.empty();
    }

    @Override
    public BasicGitConfiguration obfuscate() {
        return withPassword("");
    }

    @Override
    @JsonIgnore
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(
                name,
                String.format("%s (%s)", name, remote)
        );
    }

    @Override
    public BasicGitConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new BasicGitConfiguration(
                targetConfigurationName,
                replacementFunction.apply(remote),
                replacementFunction.apply(user),
                password,
                replacementFunction.apply(commitLink),
                replacementFunction.apply(fileAtCommitLink),
                indexationInterval,
                issueServiceConfigurationIdentifier
        );
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
                                .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.model.GitConfiguration.indexationInterval.tpl.html")
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
    public GitRepository getGitRepository() {
        Optional<UserPassword> credentials = getCredentials();
        return new GitRepository(
                TYPE,
                getName(),
                getRemote(),
                credentials.map(UserPassword::getUser).orElse(""),
                credentials.map(UserPassword::getPassword).orElse("")
        );
    }
}
