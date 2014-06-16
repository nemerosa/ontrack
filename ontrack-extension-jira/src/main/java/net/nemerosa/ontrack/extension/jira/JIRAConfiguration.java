package net.nemerosa.ontrack.extension.jira;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.support.configurations.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import static net.nemerosa.ontrack.model.form.Form.defaultText;

@Data
public class JIRAConfiguration implements UserPasswordConfiguration<JIRAConfiguration>, IssueServiceConfiguration {

    private final String name;
    private final String url;
    private final String user;
    private final String password;
    // TODO private final Set<String> excludedProjects;
    // TODO private final Set<String> excludedIssues;

    public static Form form() {
        return Form.create()
                .with(defaultText())
                .url()
                .with(Text.of("user").label("User").length(16).optional())
                .with(Password.of("password").label("Password").length(40).optional());
    }

    @Override
    public JIRAConfiguration obfuscate() {
        return new JIRAConfiguration(
                name,
                url,
                user,
                ""
        );
    }

    public Form asForm() {
        return form()
                .with(defaultText().readOnly().value(name))
                .fill("url", url)
                .fill("user", user)
                .fill("password", "");
    }

    @Override
    public JIRAConfiguration withPassword(String password) {
        return new JIRAConfiguration(
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
    public String getServiceId() {
        return JIRAServiceExtension.SERVICE;
    }
}
