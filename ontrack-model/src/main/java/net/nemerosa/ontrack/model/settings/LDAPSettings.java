package net.nemerosa.ontrack.model.settings;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
public class LDAPSettings {

    private final boolean enabled;
    private final String url;
    private final String searchBase;
    private final String searchFilter;
    private final String user;
    private final String password;
    private final String fullNameAttribute;
    private final String emailAttribute;
    // TODO Group configuration

    public Form form() {
        return Form.create()
                .with(
                        YesNo.of("enabled")
                                .label("Enable LDAP authentication")
                                .value(enabled)
                )
                .with(
                        Text.of("url")
                                .label("URL")
                                .help("URL to the LDAP server")
                                .value(url)
                )
                .with(
                        Text.of("user")
                                .label("User")
                                .help("Name of the user used to connect to the LDAP server")
                                .value(user)
                )
                .with(
                        Password.of("password")
                                .label("Password")
                                .help("Password of the user used to connect to the LDAP server")
                                .value(password)
                )
                .with(
                        Text.of("searchBase")
                                .label("Search base")
                                .help("Query to get the user")
                                .value(searchBase)
                )
                .with(
                        Text.of("searchFilter")
                                .label("Search filter")
                                .help("Filter on the user query")
                                .optional()
                                .value(searchFilter)
                )
                .with(
                        Text.of("fullNameAttribute")
                                .label("Full name attribute")
                                .help("Name of the attribute that contains the full name of the user")
                                .optional()
                                .value(fullNameAttribute)
                )
                .with(
                        Text.of("emailAttribute")
                                .label("Email attribute")
                                .help("Name of the attribute that contains the email of the user")
                                .optional()
                                .value(emailAttribute)
                )
                ;
    }

}
