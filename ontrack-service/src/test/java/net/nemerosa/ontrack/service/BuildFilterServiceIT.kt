package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.BranchEdit
import net.nemerosa.ontrack.model.security.BranchFilterMgt
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.BranchCopyRequest
import net.nemerosa.ontrack.model.structure.CopyService
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BuildFilterServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var copyService: CopyService

    @Test
    fun saveFilter_predefined() {
        // Branch
        val branch = doCreateBranch()
        // Account for the tests
        val account = doCreateAccount()
        // Creates a predefined filter for this account
        val filterCreated = asFixedAccount(account).call {
            buildFilterService.saveFilter(
                branch.id,
                false,
                "MyFilter",
                PromotionLevelBuildFilterProvider::class.java.name,
                JsonUtils.`object`().end()
            )
        }
        assertFalse(filterCreated.success, "A predefined filter cannot be saved")
    }

    /**
     * Checks that sharing a saved filter overrides the account specific one.
     */
    @Test
    fun saveFilter_shared_after_account_one() {
        // Branch
        val branch = doCreateBranch()
        // Account for the tests
        val account = doCreateAccount()
        val otherAccount = doCreateAccount()

        // Creates a filter for this account
        var ack = asFixedAccount(account).call {
            buildFilterService.saveFilter(
                branch.id,
                false,
                "MyFilter",
                StandardBuildFilterProvider::class.java.name,
                objectMapper.valueToTree(StandardBuildFilterData(count = 1))
            )
        }
        assertTrue(ack.success, "Account filter saved")

        // Makes sure we find this filter back when logged
        var filters =
            asConfigurableAccount(account).withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong())
        var filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertFalse(filter.isShared)

        // ... but it is not available for anybody else
        assertTrue(
            asConfigurableAccount(otherAccount).withView(branch)
                .call { buildFilterService.getBuildFilters(branch.id).isEmpty() },
            "Account filter not available for everybody else"
        )

        // Now, shares a filter with the same name
        ack = asConfigurableAccount(account)
            .with(branch.projectId(), ProjectView::class.java)
            .with(branch.projectId(), BranchFilterMgt::class.java)
            .call {
                buildFilterService.saveFilter(
                    branch.id,
                    true, // Sharing
                    "MyFilter",
                    StandardBuildFilterProvider::class.java.name,
                    objectMapper.valueToTree(StandardBuildFilterData(count = 1))
                )
            }
        assertTrue(ack.success, "Account filter shared")

        // Makes sure we find this filter back when logged
        filters = asFixedAccount(account).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong())
        filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertTrue(filter.isShared)

        // ... and that it is available also for not logged users
        filters = asUser().withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong(), "Account filter available for everybody else")
        filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertTrue(filter.isShared)
    }

    @Test
    fun delete_unshared_filter() {
        // Branch
        val branch = doCreateBranch()
        // Account for the tests
        val account = doCreateAccount()

        // Creates a filter for this account
        val ack = asFixedAccount(account).call {
            buildFilterService.saveFilter(
                branch.id,
                false,
                "MyFilter",
                StandardBuildFilterProvider::class.java.name,
                objectMapper.valueToTree(StandardBuildFilterData(count = 1))
            )
        }
        assertTrue(ack.success, "Account filter saved")

        // The filter is present
        var filters =
            asConfigurableAccount(account).withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong())
        val filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertFalse(filter.isShared)

        // Deletes the filter
        asFixedAccount(account).call { buildFilterService.deleteFilter(branch.id, "MyFilter") }

        // The filter is no longer there
        filters = asConfigurableAccount(account).withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(0, filters.size.toLong())
    }

    @Test
    fun delete_shared_filter() {
        // Branch
        val branch = doCreateBranch()
        // Account for the tests
        val account = doCreateAccount()

        // Creates a shared filter for this account
        val creator = asConfigurableAccount(account).withView(branch).withProjectFunction(branch, BranchFilterMgt::class.java)
        val ack = creator.call {
            buildFilterService.saveFilter(
                branch.id,
                true, // Shared
                "MyFilter",
                StandardBuildFilterProvider::class.java.name,
                objectMapper.valueToTree(StandardBuildFilterData(count = 1))
            )
        }
        assertTrue(ack.success, "Account filter saved")

        // The filter is present for this account
        var filters = creator.call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong())
        var filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertTrue(filter.isShared)

        // ... and that it is available also for other users
        filters = asUser().withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(1, filters.size.toLong(), "Account filter available for everybody else")
        filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertTrue(filter.isShared)

        // Deletes the filter
        asAdmin { buildFilterService.deleteFilter(branch.id, "MyFilter") }

        // The filter is no longer there for the account
        filters = creator.call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(0, filters.size.toLong())

        // ... not for other users
        filters = asUser().withView(branch).call { buildFilterService.getBuildFilters(branch.id) }
        assertEquals(0, filters.size.toLong(), "Account filter not available for everybody else")
    }

    @Test
    fun copyToBranch() {
        // Source branch
        val sourceBranch = doCreateBranch()
        // Target branch
        val targetBranch = doCreateBranch()
        // Account for the tests
        val account = doCreateAccount()
        // Creates a filter for this account
        val filterCreated = asFixedAccount(account).call {
            buildFilterService.saveFilter(
                sourceBranch.id,
                false,
                "MyFilter",
                StandardBuildFilterProvider::class.java.name,
                JsonUtils.`object`().with("count", 1).end()
            )
        }
        assertTrue(filterCreated.success)
        // Checks the filter is created
        var filters = asConfigurableAccount(account).with(sourceBranch.projectId(), ProjectView::class.java)
            .call { buildFilterService.getBuildFilters(sourceBranch.id) }
        assertEquals(1, filters.size.toLong())
        var filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertEquals(StandardBuildFilterProvider::class.java.name, filter.type)

        // Copy of the branch
        asUser()
            .with(sourceBranch.projectId(), ProjectView::class.java)
            .with(targetBranch.projectId(), BranchEdit::class.java)
            .call {
                copyService.copy(
                    targetBranch,
                    BranchCopyRequest(
                        sourceBranch.id,
                        emptyList()
                    )
                )
            }

        // Gets the filter on the new branch
        filters = asConfigurableAccount(account).withView(targetBranch)
            .call { buildFilterService.getBuildFilters(targetBranch.id) }
        assertEquals(1, filters.size.toLong())
        filter = filters.iterator().next()
        assertEquals("MyFilter", filter.name)
        assertEquals(StandardBuildFilterProvider::class.java.name, filter.type)
    }
}