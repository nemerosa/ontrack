package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the LDAP mappings
 */
@AcceptanceTestSuite
class ACCDSLLdapMappings extends AbstractACCDSL {

    @Test
    void 'Creation of LDAP mappings'() {
        // Creating two groups
        def g1Name = uid('G')
        def g2Name = uid('G')
        ontrack.admin.accountGroup(g1Name, "Group 1")
        ontrack.admin.accountGroup(g2Name, "Group 2")
        // Creating a mapping
        def mName = uid('M')
        ontrack.admin.ldapMapping(mName, g1Name)
        // Checking the mapping is there
        def mapping = ontrack.admin.ldapMappings.find { it.name == mName }
        assert mapping != null
        assert mapping.groupName == g1Name
        // Updating the mapping
        ontrack.admin.ldapMapping(mName, g2Name)
        // Checking the update
        mapping = ontrack.admin.ldapMappings.find { it.name == mName }
        assert mapping != null
        assert mapping.groupName == g2Name
    }

}
