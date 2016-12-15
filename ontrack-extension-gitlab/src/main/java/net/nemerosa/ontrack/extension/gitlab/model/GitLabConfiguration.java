package net.nemerosa.ontrack.extension.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPassword;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;

import java.beans.ConstructorProperties;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

/**
 * Configuration for accessing a GitLab application.
 */
@Data
public class GitLabConfiguration implements UserPasswordConfiguration<GitLabConfiguration> {

    /**
     * Name of this configuration
     */
    private final String name;

    /**
     * End point
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
     * Personal Access Token
     */
    @Wither
    private final String personalAccessToken;

    /**
     * Ignoring SSL issues?
     */
    private final boolean ignoreSslCertificate;

    @ConstructorProperties({"name", "url", "user", "password", "personalAccessToken", "ignoreSslCertificate"})
    public GitLabConfiguration(String name, String url, String user, String password, String personalAccessToken, boolean ignoreSslCertificate) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
        this.personalAccessToken = personalAccessToken;
        this.ignoreSslCertificate = ignoreSslCertificate;
    }

    @Override
    @JsonIgnore
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(
                name,
                format("%s (%s)", name, url)
        );
    }

    @Override
    public GitLabConfiguration obfuscate() {
        return this.withPersonalAccessToken("");
    }

    @Override
    public GitLabConfiguration withPassword(String password) {
        return new GitLabConfiguration(
                name,
                url,
                user,
                password,
                personalAccessToken,
                ignoreSslCertificate
        );
    }

    public static Form form() {
        return Form.create()
                .with(defaultNameField())
                .with(
                        Text.of("url")
                                .label("URL")
                                .length(250)
                                .help("URL of the GitLab engine.")
                )
                .with(
                        Text.of("user")
                                .label("User")
                                .length(16)
                )
                .with(
                        Password.of("personalAccessToken")
                                .label("Personal Access Token")
                                .length(50)
                                .optional()
                )
                .with(
                        YesNo.of("ignoreSslCertificate")
                                .label("Ignore SSL certificate")
                )
                ;
    }

    public Form asForm() {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                // .fill("personalAccessToken", personalAccessToken)
                .fill("ignoreSslCertificate", ignoreSslCertificate)
                ;
    }

    @Override
    public GitLabConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new GitLabConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password,
                personalAccessToken,
                ignoreSslCertificate
        );
    }

    @Override
    public Optional<UserPassword> getCredentials() {
        return Optional.of(
                new UserPassword(
                        user,
                        personalAccessToken
                )
        );
    }
}