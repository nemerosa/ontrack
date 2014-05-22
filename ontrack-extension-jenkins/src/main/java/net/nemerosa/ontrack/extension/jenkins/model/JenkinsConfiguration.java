package net.nemerosa.ontrack.extension.jenkins.model;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.support.Configuration;

import static net.nemerosa.ontrack.model.form.Form.defaultText;

@Data
public class JenkinsConfiguration implements Configuration<JenkinsConfiguration> {

    private final String name;
    private final String url;
    private final String user;
    private final String password;

    public static Form form() {
        return Form.create()
                .with(defaultText())
                .url()
                .with(Text.of("user").label("User").length(16).optional())
                .with(Password.of("password").label("Password").length(40).optional());
    }

    @Override
    public JenkinsConfiguration obfuscate() {
        return new JenkinsConfiguration(
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

    public JenkinsConfiguration withPassword(String password) {
        return new JenkinsConfiguration(
                name,
                url,
                user,
                password
        );
    }
}
