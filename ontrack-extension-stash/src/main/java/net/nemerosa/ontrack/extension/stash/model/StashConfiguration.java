package net.nemerosa.ontrack.extension.stash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPassword;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import org.apache.commons.lang3.StringUtils;

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
        return withPassword("");
    }

    @Override
    public StashConfiguration withPassword(String password) {
        return new StashConfiguration(
                name,
                url,
                user,
                password
        );
    }

    public static Form form() {
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
                );
    }

    public Form asForm() {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "")
                ;
    }

    @Override
    public StashConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new StashConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password
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
