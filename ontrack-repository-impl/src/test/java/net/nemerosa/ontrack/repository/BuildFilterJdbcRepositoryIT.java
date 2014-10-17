package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuildFilterJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BuildFilterJdbcRepository repository;

    private Branch branch;
    private Account account;

    @Before
    public void create_branch() {
        // Creates a project
        Project project = structureRepository.newProject(Project.of(nameDescription()));
        // Creates a branch for this project
        branch = structureRepository.newBranch(Branch.of(project, nameDescription()));
        // Creates an account
        AuthenticationSource authenticationSource = mock(AuthenticationSource.class);
        when(authenticationSource.getId()).thenReturn("test");
        account = accountRepository.newAccount(
                Account.of(
                        "test",
                        "Test user",
                        "test@test.com",
                        SecurityRole.USER,
                        authenticationSource
                )
        );
    }

    @Test
    public void save_account_build_filter() {
        Ack ack = repository.save(
                OptionalInt.of(account.id()),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.object().with("test", 1).end()
        );
        assertTrue(ack.isSuccess());

        // Gets the list for this branch AND account
        Collection<TBuildFilter> list = repository.findForBranch(OptionalInt.of(account.id()), branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets the list for this branch
        list = repository.findForBranch(branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets this filter
        Optional<TBuildFilter> filter = repository.findByBranchAndName(account.id(), branch.id(), "Test");
        assertTrue(filter.isPresent());
        assertEquals(
                new TBuildFilter(
                        OptionalInt.of(account.id()),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.object().with("test", 1).end()
                ),
                filter.get()
        );
    }

    @Test
    public void save_shared_build_filter() {
        Ack ack = repository.save(
                OptionalInt.empty(),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.object().with("test", 1).end()
        );
        assertTrue(ack.isSuccess());

        // Gets the list for this branch AND account
        Collection<TBuildFilter> list = repository.findForBranch(OptionalInt.of(account.id()), branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets the list for this branch
        list = repository.findForBranch(branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets this filter
        Optional<TBuildFilter> filter = repository.findByBranchAndName(account.id(), branch.id(), "Test");
        assertTrue(filter.isPresent());
        assertEquals(
                new TBuildFilter(
                        OptionalInt.empty(),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.object().with("test", 1).end()
                ),
                filter.get()
        );
    }

    @Test
    public void save_shared_build_filter_same_name() {
        Ack ack = repository.save(
                OptionalInt.empty(),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.object().with("test", 1).end()
        );
        assertTrue(ack.isSuccess());
        ack = repository.save(
                OptionalInt.of(account.id()),
                branch.id(),
                "Test",
                "TestFilterType",
                JsonUtils.object().with("test", 1).end()
        );
        assertTrue(ack.isSuccess());

        // Gets the list for this branch AND account
        Collection<TBuildFilter> list = repository.findForBranch(OptionalInt.of(account.id()), branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        ),
                        new TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets the list for this branch
        list = repository.findForBranch(branch.id());
        assertEquals(
                Arrays.asList(
                        new TBuildFilter(
                                OptionalInt.of(account.id()),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        ),
                        new TBuildFilter(
                                OptionalInt.empty(),
                                branch.id(),
                                "Test",
                                "TestFilterType",
                                JsonUtils.object().with("test", 1).end()
                        )
                ),
                list
        );

        // Gets this filter
        Optional<TBuildFilter> filter = repository.findByBranchAndName(account.id(), branch.id(), "Test");
        assertTrue(filter.isPresent());
        assertEquals(
                new TBuildFilter(
                        OptionalInt.of(account.id()),
                        branch.id(),
                        "Test",
                        "TestFilterType",
                        JsonUtils.object().with("test", 1).end()
                ),
                filter.get()
        );
    }

}
