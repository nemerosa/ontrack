package net.nemerosa.ontrack.extension.stash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPassword;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

@Data
public class StashConfiguration implements UserPasswordConfiguration<StashConfiguration> {

    /**
     * Name of this configuration
     */
    private final String name;

    /**
     * Stash URL
     */
    private final String url;

    /**
     * User name
     */
    private final String user;

    /**
     * User password
     */
    private final String password;

    /**
     * Indexation interval
     */
    @Deprecated
    private final int indexationInterval;

    /**
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    @Deprecated
    private final String issueServiceConfigurationIdentifier;

    @Override
    @JsonIgnore
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(
                name,
                format("%s (%s)", name, url)
        );
    }

    @Override
    public StashConfiguration obfuscate() {
        return this;
    }

    @Override
    public StashConfiguration withPassword(String password) {
        return new StashConfiguration(
                name,
                url,
                user,
                password,
                indexationInterval,
                issueServiceConfigurationIdentifier
        );
    }

    public static Form form(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
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
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
                .fill("indexationInterval", indexationInterval)
                .fill("issueServiceConfigurationIdentifier", issueServiceConfigurationIdentifier)
                ;
    }

    @Override
    public StashConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new StashConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password,
                indexationInterval,
                issueServiceConfigurationIdentifier
        );
    }

    @Override
    @JsonIgnore
    public Optional<UserPassword> getCredentials() {
        if (StringUtils.isNotBlank(user)) {
            return Optional.of(
                    new UserPassword(
                            user,
                            password
                    )
            );
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks if this configuration denotes any BitBucket Cloud instance
     */
    @JsonIgnore
    public boolean isCloud() {
        return StringUtils.contains(url, "bitbucket.org");
    }
}
