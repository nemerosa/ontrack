package net.nemerosa.ontrack.extension.ldap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LDAPSettings {

    private final boolean enabled;
    private final String url;
    private final String searchBase;
    private final String searchFilter;
    private final String user;
    @Wither
    private final String password;
    @Wither
    private final String fullNameAttribute;
    @Wither
    private final String emailAttribute;
    @Wither
    private final String groupAttribute;
    @Wither
    private final String groupFilter;
    /**
     * The ID of the attribute which contains the name for a group
     */
    @Wither
    private String groupNameAttribute = "cn";

    /**
     * The base DN from which the search for group membership should be performed
     */
    @Wither
    private String groupSearchBase;

    /**
     * The pattern to be used for the user search. {0} is the user's DN
     */
    @Wither
    private String groupSearchFilter = "(member={0})";

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
            "",
            "",
            "",
            ""
    );

}
