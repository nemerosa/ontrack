package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.BranchEdit;
import net.nemerosa.ontrack.model.security.BranchFilterMgt;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.BranchCopyRequest;
import net.nemerosa.ontrack.model.structure.CopyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class BuildFilterServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private BuildFilterService buildFilterService;

    @Autowired
    private CopyService copyService;

    @Test
    public void saveFilter_predefined() throws Exception {
        // Branch
        Branch branch = doCreateBranch();
        // Account for the tests
        Account account = doCreateAccount();
        // Creates a predefined filter for this account
        Ack filterCreated = asAccount(account).call(() ->
                        buildFilterService.saveFilter(
                                branch.getId(),
                                false,
                                "MyFilter",
                                PromotionLevelBuildFilterProvider.class.getName(),
                                JsonUtils.object().end()
                        )
        );
        assertFalse("A predefined filter cannot be saved", filterCreated.isSuccess());
    }

    /**
     * Checks that sharing a saved filter overrides the account specific one.
     */
    @Test
    public void saveFilter_shared_after_account_one() throws Exception {
        // Branch
        Branch branch = doCreateBranch();
        // Account for the tests
        Account account = doCreateAccount();

        // Creates a filter for this account
        Ack ack = asAccount(account).call(() ->
                        buildFilterService.saveFilter(
                                branch.getId(),
                                false,
                                "MyFilter",
                                NamedBuildFilterProvider.class.getName(),
                                objectMapper.valueToTree(NamedBuildFilterData.of("1"))
                        )
        );
        assertTrue("Account filter saved", ack.isSuccess());

        // Makes sure we find this filter back when logged
        Collection<BuildFilterResource<?>> filters = asAccount(account).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(1, filters.size());
        BuildFilterResource<?> filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertFalse(filter.isShared());

        // ... but it is not available for anybody else
        assertTrue(
                "Account filter not available for everybody else",
                asUser().withId(10).withView(branch).call(() ->
                                buildFilterService.getBuildFilters(branch.getId()).isEmpty()
                )
        );

        // Now, shares a filter with the same name
        ack = asAccount(account)
                .with(branch.projectId(), ProjectView.class)
                .with(branch.projectId(), BranchFilterMgt.class)
                .call(() ->
                                buildFilterService.saveFilter(
                                        branch.getId(),
                                        true, // Sharing
                                        "MyFilter",
                                        NamedBuildFilterProvider.class.getName(),
                                        objectMapper.valueToTree(NamedBuildFilterData.of("1"))
                                )
                );
        assertTrue("Account filter shared", ack.isSuccess());

        // Makes sure we find this filter back when logged
        filters = asAccount(account).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(1, filters.size());
        filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertTrue(filter.isShared());

        // ... and that it is available also for not logged users
        filters = asUser().withId(10).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals("Account filter available for everybody else", 1, filters.size());
        filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertTrue(filter.isShared());
    }

    @Test
    public void delete_unshared_filter() throws Exception {
        // Branch
        Branch branch = doCreateBranch();
        // Account for the tests
        Account account = doCreateAccount();

        // Creates a filter for this account
        Ack ack = asAccount(account).call(() ->
                        buildFilterService.saveFilter(
                                branch.getId(),
                                false,
                                "MyFilter",
                                NamedBuildFilterProvider.class.getName(),
                                objectMapper.valueToTree(NamedBuildFilterData.of("1"))
                        )
        );
        assertTrue("Account filter saved", ack.isSuccess());

        // The filter is present
        Collection<BuildFilterResource<?>> filters = asAccount(account).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(1, filters.size());
        BuildFilterResource<?> filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertFalse(filter.isShared());

        // Deletes the filter
        asAccount(account).call(() -> buildFilterService.deleteFilter(branch.getId(), "MyFilter"));

        // The filter is no longer there
        filters = asAccount(account).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(0, filters.size());
    }

    @Test
    public void delete_shared_filter() throws Exception {
        // Branch
        Branch branch = doCreateBranch();
        // Account for the tests
        Account account = doCreateAccount();

        // Creates a shared filter for this account
        Ack ack = asAccount(account).with(branch, BranchFilterMgt.class).call(() ->
                        buildFilterService.saveFilter(
                                branch.getId(),
                                true, // Shared
                                "MyFilter",
                                NamedBuildFilterProvider.class.getName(),
                                objectMapper.valueToTree(NamedBuildFilterData.of("1"))
                        )
        );
        assertTrue("Account filter saved", ack.isSuccess());

        // The filter is present for this account
        Collection<BuildFilterResource<?>> filters = asAccount(account).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(1, filters.size());
        BuildFilterResource<?> filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertTrue(filter.isShared());

        // ... and that it is available also for not logged users
        filters = asUser().withId(10).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals("Account filter available for everybody else", 1, filters.size());
        filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertTrue(filter.isShared());

        // Deletes the filter
        asAccount(account).call(() -> buildFilterService.deleteFilter(branch.getId(), "MyFilter"));

        // The filter is no longer there for the account
        filters = asAccount(account).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals(0, filters.size());

        // ... not for unlogged users
        filters = asUser().withId(10).withView(branch).call(() -> buildFilterService.getBuildFilters(branch.getId()));
        assertEquals("Account filter not available for everybody else", 0, filters.size());
    }

    @Test
    public void copyToBranch() throws Exception {
        // Source branch
        Branch sourceBranch = doCreateBranch();
        // Target branch
        Branch targetBranch = doCreateBranch();
        // Account for the tests
        Account account = doCreateAccount();
        // Creates a filter for this account
        Ack filterCreated = asAccount(account).call(() ->
                        buildFilterService.saveFilter(
                                sourceBranch.getId(),
                                false,
                                "MyFilter",
                                StandardBuildFilterProvider.class.getName(),
                                JsonUtils.object().with("count", 1).end()
                        )
        );
        assertTrue(filterCreated.isSuccess());
        // Checks the filter is created
        Collection<BuildFilterResource<?>> filters = asAccount(account).with(sourceBranch.projectId(), ProjectView.class).call(() -> buildFilterService.getBuildFilters(sourceBranch.getId()));
        assertEquals(1, filters.size());
        BuildFilterResource<?> filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertEquals(StandardBuildFilterProvider.class.getName(), filter.getType());

        // Copy of the branch
        asUser()
                .with(sourceBranch.projectId(), ProjectView.class)
                .with(targetBranch.projectId(), BranchEdit.class)
                .call(() ->
                                copyService.copy(
                                        targetBranch,
                                        new BranchCopyRequest(
                                                sourceBranch.getId(),
                                                Collections.emptyList()
                                        )
                                )
                );

        // Gets the filter on the new branch
        filters = asAccount(account).withView(targetBranch).call(() -> buildFilterService.getBuildFilters(targetBranch.getId()));
        assertEquals(1, filters.size());
        filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertEquals(StandardBuildFilterProvider.class.getName(), filter.getType());
    }
}