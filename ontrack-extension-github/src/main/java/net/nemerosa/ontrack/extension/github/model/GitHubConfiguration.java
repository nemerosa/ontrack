package net.nemerosa.ontrack.extension.github.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.support.configurations.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

@Data
public class GitHubConfiguration implements UserPasswordConfiguration<GitHubConfiguration> {

    /**
     * Name of this configuration
     */
    private final String name;

    /**
     * Repository name
     */
    private final String repository;

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
    private final int indexationInterval;

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(
                name,
                String.format("%s (%s)", name, repository)
        );
    }

    @Override
    public GitHubConfiguration obfuscate() {
        return this;
    }

    @Override
    public GitHubConfiguration withPassword(String password) {
        return new GitHubConfiguration(
                name,
                repository,
                user,
                password,
                indexationInterval
        );
    }

    public static Form form() {
        return Form.create()
                .with(defaultNameField())
                .with(
                        Text.of("repository")
                                .label("Repository")
                                .length(100)
                                .regex("[A-Za-z0-9_\\.\\-]+")
                                .validation("Repository is required and must be a GitHub repository (account/repository)."))
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
                                .help("Interval (in minutes) between each indexation of the GitHub repository. A " +
                                        "zero value indicates that no indexation must take place automatically and they " +
                                        "have to be triggered manually.")
                );
    }

    public Form asForm() {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("repository", repository)
                .fill("user", user)
                .fill("password", "")
                .fill("indexationInterval", indexationInterval)
                ;
    }
}
