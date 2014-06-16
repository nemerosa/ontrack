package net.nemerosa.ontrack.extension.jira;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.support.configurations.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.regex.Pattern;

import static net.nemerosa.ontrack.model.form.Form.defaultText;

@Data
public class JIRAConfiguration implements UserPasswordConfiguration<JIRAConfiguration>, IssueServiceConfiguration {

    public static final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");

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
    @JsonIgnore
    public String getServiceId() {
        return JIRAServiceExtension.SERVICE;
    }

    public boolean isIssue(String token) {
        return ISSUE_PATTERN.matcher(token).matches()
                && !isIssueExcluded(token);
    }

    private boolean isIssueExcluded(String token) {
        return false;
        // TODO return excludedIssues.contains(token)
        // TODO        || excludedProjects.contains(StringUtils.substringBefore(token, "-"));
    }
}
