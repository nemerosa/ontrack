package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.Account.Companion.of
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Transactional
class BuildFilterJdbcRepositoryIT : AbstractRepositoryTestSupport() {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var repository: BuildFilterJdbcRepository

    private lateinit var branch: Branch
    private lateinit var accountName: String
    private lateinit var account: Account

    @BeforeEach
    fun create_branch() {
        branch = do_create_branch()
        // Creates an account
        val authenticationSource = AuthenticationSource(
            provider = "test",
            key = "test",
            name = "Test authentication source",
            isAllowingPasswordChange = false
        )
        accountName = TestUtils.uid("A")
        account = accountRepository.newAccount(
            of(
                accountName,
                "Test user",
                "test@test.com",
                SecurityRole.USER,
            )
        )
    }

    @Test
    fun save_account_build_filter() {
        val ack = repository.save(
            account.id(),
            branch.id(),
            "Test",
            "TestFilterType",
            mapOf("test" to 1).asJson()
        )
        assertTrue(ack.success)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(account.id(), branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    account.id(),
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    account.id(),
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        assertNotNull(filter) {
            assertEquals(
                TBuildFilter(
                    account.id(),
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                ),
                it
            )
        }
    }

    @Test
    fun save_shared_build_filter() {
        val ack = repository.save(
            accountId = null,
            branchId = branch.id(),
            name = "Test",
            type = "TestFilterType",
            data = mapOf("test" to 1).asJson()
        )
        assertTrue(ack.success)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(account.id(), branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        assertNotNull(filter) {
            assertEquals(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                ),
                it
            )
        }
    }

    @Test
    fun save_shared_build_filter_same_name() {
        var ack = repository.save(
            null,
            branch.id(),
            "Test",
            "TestFilterType",
            mapOf("test" to 1).asJson()
        )
        assertTrue(ack.success)
        ack = repository.save(
            account.id(),
            branch.id(),
            "Test",
            "TestFilterType",
            mapOf("test" to 1).asJson()
        )
        assertTrue(ack.success)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(account.id(), branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                ),
                TBuildFilter(
                    account.id(),
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        assertEquals(
            listOf(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                ),
                TBuildFilter(
                    account.id(),
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                )
            ),
            list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        assertNotNull(filter) {
            assertEquals(
                TBuildFilter(
                    null,
                    branch.id(),
                    "Test",
                    "TestFilterType",
                    mapOf("test" to 1).asJson()
                ),
                it
            )
        }
    }
}