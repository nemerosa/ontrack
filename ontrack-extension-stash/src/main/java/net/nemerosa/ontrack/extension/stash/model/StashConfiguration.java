package net.nemerosa.ontrack.extension.stash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPassword;
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
     * ID to the {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration} associated
     * with this repository.
     */
    @Wither
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
                issueServiceConfigurationIdentifier
        );
    }

    public static Form form(List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations) {
        return Form.create()
                .with(defaultNameField())
                .with(
                        Text.of("url")
                                .label("URL")
                                .validation("URL to the Stash instance"))
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

}
