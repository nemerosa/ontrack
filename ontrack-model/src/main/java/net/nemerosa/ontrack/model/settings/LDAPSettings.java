package net.nemerosa.ontrack.model.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LDAPSettings {

    private final boolean enabled;
    private final String url;
    private final String searchBase;
    private final String searchFilter;
    private final String user;
    private final String password;
    @Wither
    private final String fullNameAttribute;
    @Wither
    private final String emailAttribute;
    @Wither
    private final String groupAttribute;
    @Wither
    private final String groupFilter;

    public Form form() {
        return Form.create()
                .with(
                        YesNo.of("enabled")
                                .label("Enable LDAP authentication")
                                .value(enabled)
                )
                .with(
                        Text.of("url")
                                .visibleIf("enabled")
                                .label("URL")
                                .help("URL to the LDAP server")
                                .value(url)
                )
                .with(
                        Text.of("user")
                                .visibleIf("enabled")
                                .label("User")
                                .help("Name of the user used to connect to the LDAP server")
                                .optional()
                                .value(user)
                )
                .with(
                        Password.of("password")
                                .visibleIf("enabled")
                                .label("Password")
                                .help("Password of the user used to connect to the LDAP server")
                                .optional()
                                .value("") // Password never sent to the client
                )
                .with(
                        Text.of("searchBase")
                                .visibleIf("enabled")
                                .label("Search base")
                                .help("Query to get the user")
                                .optional()
                                .value(searchBase)
                )
                .with(
                        Text.of("searchFilter")
                                .visibleIf("enabled")
                                .label("Search filter")
                                .help("Filter on the user query")
                                .optional()
                                .value(searchFilter)
                )
                .with(
                        Text.of("fullNameAttribute")
                                .visibleIf("enabled")
                                .label("Full name attribute")
                                .help("Name of the attribute that contains the full name of the user")
                                .optional()
                                .value(fullNameAttribute)
                )
                .with(
                        Text.of("emailAttribute")
                                .visibleIf("enabled")
                                .label("Email attribute")
                                .help("Name of the attribute that contains the email of the user")
                                .optional()
                                .value(emailAttribute)
                )
                .with(
                        Text.of("groupAttribute")
                                .visibleIf("enabled")
                                .label("Group attribute")
                                .help("Name of the attribute that contains the groups the user belongs to (defaults to memberOf)")
                                .optional()
                                .value(groupAttribute)
                )
                .with(
                        Text.of("groupFilter")
                                .visibleIf("enabled")
                                .label("Group filter")
                                .help("Name of the OU field used to filter groups a user belongs to (optional)")
                                .optional()
                                .value(groupFilter)
                )
                ;
    }

    public static final LDAPSettings NONE = new LDAPSettings(
            false,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    );

}
