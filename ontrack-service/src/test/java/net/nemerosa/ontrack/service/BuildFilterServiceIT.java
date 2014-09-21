package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.BranchEdit;
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
                                "MyFilter",
                                PromotionLevelBuildFilterProvider.class.getName(),
                                JsonUtils.object().end()
                        )
        );
        assertFalse("A predefined filter cannot be saved", filterCreated.isSuccess());
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
                                "MyFilter",
                                StandardBuildFilterProvider.class.getName(),
                                JsonUtils.object().with("count", 1).end()
                        )
        );
        assertTrue(filterCreated.isSuccess());
        // Checks the filter is created
        Collection<BuildFilterResource<?>> filters = asAccount(account).call(() -> buildFilterService.getBuildFilters(sourceBranch.getId()));
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
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                Collections.emptyList()
                                        )
                                )
                );

        // Gets the filter on the new branch
        filters = asAccount(account).call(() -> buildFilterService.getBuildFilters(targetBranch.getId()));
        assertEquals(1, filters.size());
        filter = filters.iterator().next();
        assertEquals("MyFilter", filter.getName());
        assertEquals(StandardBuildFilterProvider.class.getName(), filter.getType());
    }
}