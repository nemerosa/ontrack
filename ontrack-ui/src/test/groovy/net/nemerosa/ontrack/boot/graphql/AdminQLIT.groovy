package net.nemerosa.ontrack.boot.graphql

import graphql.GraphQLException
import net.nemerosa.ontrack.model.security.AccountGroupManagement
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class AdminQLIT extends AbstractQLITSupport {

    @Autowired
    private AccountService accountService

    @Test(expected = GraphQLException)
    void 'List of groups needs authorisation'() {
        run("""{ accountGroups { id } }""")
    }

    @Test
    void 'List of groups'() {
        asUser().with(AccountGroupManagement).call {
            def g = accountService.createGroup(NameDescription.nd(TestUtils.uid('G'), '')).id()
            def data = run("""{ accountGroups { id name } }""")
            assert data.accountGroups.find { it.id == g } != null
        }

    }

}
