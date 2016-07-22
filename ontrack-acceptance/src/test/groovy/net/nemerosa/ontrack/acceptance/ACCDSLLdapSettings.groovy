package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.After
import org.junit.Test

/**
 * Acceptance tests for the LDAP settings
 */
@AcceptanceTestSuite
class ACCDSLLdapSettings extends AbstractACCDSL {

    @After
    void 'Restore defaults'() {
        ontrack.config.ldapSettings = [
                enabled: false,
        ]
    }

    @Test
    void 'LDAP not enabled by default'() {
        // By default, the LDAP authentication is not enabled
        def settings = ontrack.config.ldapSettings
        assert !settings.data.enabled
    }

    @Test
    void 'LDAP settings'() {
        // Settings the LDAP parameters
        ontrack.config.ldapSettings = [
                enabled     : true,
                url         : 'ldaps://ldap.company.com:636',
                searchBase  : 'dc=company,dc=com',
                searchFilter: '(sAMAccountName={0})',
                user        : 'service',
                password    : 'secret',
        ]
        // Gets the LDAP parameters
        def settings = ontrack.config.ldapSettings
        // Checks values
        assert settings.data.enabled
        assert settings.data.url == 'ldaps://ldap.company.com:636'
        assert settings.data.searchBase == 'dc=company,dc=com'
        assert settings.data.searchFilter == '(sAMAccountName={0})'
        assert settings.data.user == 'service'
        assert settings.data.password == '' // Not sent back
        assert settings.data.fullNameAttribute == ''
        assert settings.data.emailAttribute == ''
        assert settings.data.groupAttribute == ''
        assert settings.data.groupFilter == ''
    }

}
