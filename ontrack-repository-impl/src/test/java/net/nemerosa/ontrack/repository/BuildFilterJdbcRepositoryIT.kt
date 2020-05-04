package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.Account.Companion.of
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
class BuildFilterJdbcRepositoryIT : AbstractRepositoryTestSupport() {
    @Autowired
    private lateinit var accountRepository: AccountRepository
    @Autowired
    private lateinit var repository: BuildFilterJdbcRepository

    private lateinit var branch: Branch
    private lateinit var accountName: String
    private lateinit var account: Account
    @Before
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
                        authenticationSource
                )
        )
    }

    @Test
    fun save_account_build_filter() {
        val ack = repository.save(
                OptionalInt.of(account.id()),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.`object`().with("test", 1).end()
        )
        Assert.assertTrue(ack.isSuccess)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(OptionalInt.of(account.id()), branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        Assert.assertTrue(filter.isPresent)
        Assert.assertEquals(
                TBuildFilter(
                        OptionalInt.of(account.id()),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.`object`().with("test", 1).end()
                ),
                filter.get()
        )
    }

    @Test
    fun save_shared_build_filter() {
        val ack = repository.save(
                OptionalInt.empty(),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.`object`().with("test", 1).end()
        )
        Assert.assertTrue(ack.isSuccess)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(OptionalInt.of(account.id()), branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        Assert.assertTrue(filter.isPresent)
        Assert.assertEquals(
                TBuildFilter(
                        OptionalInt.empty(),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.`object`().with("test", 1).end()
                ),
                filter.get()
        )
    }

    @Test
    fun save_shared_build_filter_same_name() {
        var ack = repository.save(
                OptionalInt.empty(),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.`object`().with("test", 1).end()
        )
        Assert.assertTrue(ack.isSuccess)
        ack = repository.save(
                OptionalInt.of(account.id()),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.`object`().with("test", 1).end()
        )
        Assert.assertTrue(ack.isSuccess)
        // Gets the list for this branch AND account
        var list = repository.findForBranch(OptionalInt.of(account.id()), branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        ),
                        TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets the list for this branch
        list = repository.findForBranch(branch.id())
        Assert.assertEquals(
                Arrays.asList(
                        TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        ),
                        TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.`object`().with("test", 1).end()
                        )
                ),
                list
        )
        // Gets this filter
        val filter = repository.findByBranchAndName(account.id(), branch.id(), "Test")
        Assert.assertTrue(filter.isPresent)
        Assert.assertEquals(
                TBuildFilter(
                        OptionalInt.empty(),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.`object`().with("test", 1).end()
                ),
                filter.get()
        )
    }
}