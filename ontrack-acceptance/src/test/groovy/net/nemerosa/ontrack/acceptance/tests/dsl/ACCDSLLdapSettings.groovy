package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.dsl.v4.LDAPSettings
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * LDAP settings and group mappings.
 */
class ACCDSLLdapSettings extends AbstractACCDSL {

    @Test
    void 'LDAP group mappings'() {
        def ldapGroup = uid('L')
        def ontrackGroup = uid('G')
        ontrack.admin.accountGroup(ontrackGroup, "Test group")
        withLdapConfigured {
            ontrack.admin.setGroupMapping("ldap", "", ldapGroup, ontrackGroup)
            // Checks the mapping has been created
            def mappings = ontrack.admin.getMappedGroups("ldap", "")
            def mapping = mappings.find { it.name == ldapGroup }
            assert mapping != null: "LDAP mapping found"
            assert mapping.groupName == ontrackGroup
        }
    }

    @Test
    void 'LDAP group mappings using the ldapMapping method alias'() {
        def ldapGroup = uid('L')
        def ontrackGroup = uid('G')
        ontrack.admin.accountGroup(ontrackGroup, "Test group")
        withLdapConfigured {
            ontrack.admin.ldapMapping(ldapGroup, ontrackGroup)
            // Checks the mapping has been created
            def mappings = ontrack.admin.getMappedGroups("ldap", "")
            def mapping = mappings.find { it.name == ldapGroup }
            assert mapping != null: "LDAP mapping found"
            assert mapping.groupName == ontrackGroup
        }
    }

    private void withLdapConfigured(Closure code) {
        def oldSettings = ontrack.config.ldapSettings
        try {
            ontrack.config.ldapSettings = new LDAPSettings(
                    true,
                    "ldap://some-ldap",
                    "searchBase",
                    "searchFilter",
                    "user",
                    "verysecret",
            )
            code()
        } finally {
            ontrack.config.ldapSettings = oldSettings
        }
    }

}
