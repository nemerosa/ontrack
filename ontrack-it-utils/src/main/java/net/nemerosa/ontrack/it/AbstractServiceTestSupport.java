package net.nemerosa.ontrack.it;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;

import static net.nemerosa.ontrack.test.TestUtils.uid;

public abstract class AbstractServiceTestSupport extends AbstractITTestSupport {

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected StructureService structureService;

    protected Account doCreateAccount() throws Exception {
        return asUser().with(AccountManagement.class).call(() -> {
            String name = uid("A");
            return accountService.create(
                    new AccountInput(
                            name,
                            "Test " + name,
                            name + "@test.com",
                            "test",
                            Collections.emptyList()
                    )
            );
        });
    }

    protected Account doCreateAccountWithGlobalRole(String role) throws Exception {
        Account account = doCreateAccount();
        return asUser().with(AccountManagement.class).call(() -> {
            accountService.saveGlobalPermission(
                    PermissionTargetType.ACCOUNT,
                    account.id(),
                    new PermissionInput(role)
            );
            return accountService.withACL(
                    AuthenticatedAccount.of(account)
            );
        });
    }

    protected Project doCreateProject() throws Exception {
        return doCreateProject(nameDescription());
    }

    protected Project doCreateProject(NameDescription nameDescription) throws Exception {
        return asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription)
        ));
    }

    protected Branch doCreateBranch() throws Exception {
        return doCreateBranch(doCreateProject(), nameDescription());
    }

    protected Branch doCreateBranch(Project project, NameDescription nameDescription) throws Exception {
        return asUser().with(project.id(), BranchCreate.class).call(() -> structureService.newBranch(
                Branch.of(project, nameDescription)
        ));
    }

    protected Build doCreateBuild() throws Exception {
        return doCreateBuild(doCreateBranch(), nameDescription());
    }

    protected Build doCreateBuild(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.projectId(), BuildCreate.class).call(() -> structureService.newBuild(
                Build.of(
                        branch,
                        nameDescription,
                        Signature.of("test")
                )
        ));
    }

    protected PromotionLevel doCreatePromotionLevel() throws Exception {
        return doCreatePromotionLevel(doCreateBranch(), nameDescription());
    }

    protected PromotionLevel doCreatePromotionLevel(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.projectId(), PromotionLevelCreate.class).call(() -> structureService.newPromotionLevel(
                PromotionLevel.of(
                        branch,
                        nameDescription
                )
        ));
    }

    protected ValidationStamp doCreateValidationStamp() throws Exception {
        return doCreateValidationStamp(doCreateBranch(), nameDescription());
    }

    public ValidationStamp doCreateValidationStamp(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.getProject().id(), ValidationStampCreate.class).call(() ->
                structureService.newValidationStamp(
                        ValidationStamp.of(
                                branch,
                                nameDescription
                        )
                )
        );
    }

    protected PromotionRun doPromote(Build build, PromotionLevel promotionLevel, String description) throws Exception {
        return asUser().with(build.projectId(), PromotionRunCreate.class).call(() ->
                structureService.newPromotionRun(
                        PromotionRun.of(
                                build,
                                promotionLevel,
                                Signature.of("test"),
                                description
                        )
                )
        );
    }

    protected UserCall asUser() {
        return new UserCall();
    }

    protected AccountCall asAccount(Account account) {
        return new AccountCall(account);
    }

    protected AccountCall asGlobalRole(String role) throws Exception {
        return new AccountCall(doCreateAccountWithGlobalRole(role));
    }

    protected <T> T view(ProjectEntity projectEntity, Callable<T> callable) throws Exception {
        return asUser().with(projectEntity.projectId(), ProjectView.class).call(callable);
    }

    protected interface ContextCall {
        <T> T call(Callable<T> call) throws Exception;
    }

    protected static abstract class AbstractContextCall implements ContextCall {

        @Override
        public <T> T call(Callable<T> call) throws Exception {
            // Gets the current context
            SecurityContext oldContext = SecurityContextHolder.getContext();
            try {
                // Sets the new context
                contextSetup();
                // Call
                return call.call();
            } finally {
                // Restores the context
                SecurityContextHolder.setContext(oldContext);
            }
        }

        public void execute(Runnable task) throws Exception {
            call(() -> {
                task.run();
                return null;
            });
        }

        protected abstract void contextSetup();
    }

    protected static class AccountCall<T extends AccountCall<T>> extends AbstractContextCall {

        protected final Account account;

        public AccountCall(Account account) {
            this.account = account;
        }

        public AccountCall(String name, SecurityRole role) {
            this(Account.of(name, name, name + "@test.com", role, AuthenticationSource.none()));
        }

        public T with(Class<? extends GlobalFunction> fn) {
            account.withGlobalRole(
                    Optional.of(
                            new GlobalRole(
                                    "test", "Test global role", "",
                                    Collections.singleton(fn),
                                    Collections.emptySet()
                            )
                    )
            );
            //noinspection unchecked
            return (T) this;
        }

        public T with(int projectId, Class<? extends ProjectFunction> fn) {
            account.withProjectRole(
                    new ProjectRoleAssociation(
                            projectId,
                            new ProjectRole(
                                    "test", "Test", "",
                                    Collections.singleton(fn)
                            )
                    )
            );
            //noinspection unchecked
            return (T) this;
        }

        public T with(ProjectEntity e, Class<? extends ProjectFunction> fn) {
            return with(e.projectId(), fn);
        }

        public T withView(ProjectEntity e) {
            return with(e, ProjectView.class);
        }

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                    (AccountHolder) () -> account,
                    "",
                    account.getRole().name()
            );
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class UserCall extends AccountCall<UserCall> {

        public UserCall() {
            super("user", SecurityRole.USER);
        }

        public AccountCall withId(int id) {
            return new AccountCall(account.withId(ID.of(id)));
        }
    }
}
