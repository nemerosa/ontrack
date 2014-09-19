package net.nemerosa.ontrack.extension.artifactory.configuration;

import lombok.Data;
import net.nemerosa.ontrack.extension.support.configurations.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.function.Function;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

@Data
public class ArtifactoryConfiguration implements UserPasswordConfiguration<ArtifactoryConfiguration> {

    private final String name;
    private final String url;
    private final String user;
    private final String password;

    public static Form form() {
        return Form.create()
                .with(defaultNameField())
                .url()
                .with(Text.of("user").label("User").length(16).optional())
                .with(Password.of("password").label("Password").length(40).optional());
    }

    @Override
    public ArtifactoryConfiguration obfuscate() {
        return new ArtifactoryConfiguration(
                name,
                url,
                user,
                ""
        );
    }

    public Form asForm() {
        return form()
                .with(defaultNameField().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "");
    }

    @Override
    public ArtifactoryConfiguration withPassword(String password) {
        return new ArtifactoryConfiguration(
                name,
                url,
                user,
                password
        );
    }

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor(name, name);
    }


    @Override
    public ArtifactoryConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new ArtifactoryConfiguration(
                targetConfigurationName,
                replacementFunction.apply(url),
                replacementFunction.apply(user),
                password
        );
    }
}
