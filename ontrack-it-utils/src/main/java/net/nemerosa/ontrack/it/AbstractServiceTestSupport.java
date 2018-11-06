package net.nemerosa.ontrack.it;

import com.google.common.collect.ImmutableSet;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.settings.SettingsManagerService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.test.TestUtils.uid;

public abstract class AbstractServiceTestSupport extends AbstractITTestSupport {

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected StructureService structureService;

    @Autowired
    protected PropertyService propertyService;

    @Autowired
    private SettingsManagerService settingsManagerService;

    protected AccountGroup doCreateAccountGroup() throws Exception {
        return asUser().with(AccountGroupManagement.class).call(() -> {
            String name = uid("G");
            return accountService.createGroup(
                    NameDescription.nd(name, "")
            );
        });
    }

    protected Account doCreateAccount() throws Exception {
        return doCreateAccount(Collections.emptyList());
    }

    protected Account doCreateAccount(AccountGroup accountGroup) throws Exception {
        return doCreateAccount(Collections.singletonList(accountGroup));
    }

    protected Account doCreateAccount(List<AccountGroup> accountGroups) throws Exception {
        return asUser().with(AccountManagement.class).call(() -> {
            String name = uid("A");
            return accountService.create(
                    new AccountInput(
                            name,
                            "Test " + name,
                            name + "@test.com",
                            "test",
                            accountGroups.stream().map(Entity::id).collect(Collectors.toList())
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

    protected Account doCreateAccountWithProjectRole(Project project, String role) throws Exception {
        Account account = doCreateAccount();
        return asUser().with(project, ProjectAuthorisationMgt.class).call(() -> {
            accountService.saveProjectPermission(
                    project.getId(),
                    PermissionTargetType.ACCOUNT,
                    account.id(),
                    new PermissionInput(role)
            );
            return accountService.withACL(
                    AuthenticatedAccount.of(account)
            );
        });
    }

    protected AccountGroup doCreateAccountGroupWithGlobalRole(String role) throws Exception {
        AccountGroup group = doCreateAccountGroup();
        return asUser().with(AccountGroupManagement.class).call(() -> {
            accountService.saveGlobalPermission(
                    PermissionTargetType.GROUP,
                    group.id(),
                    new PermissionInput(role)
            );
            return group;
        });
    }

    protected <T> void setProperty(ProjectEntity projectEntity, Class<? extends PropertyType<T>> propertyTypeClass, T data) throws Exception {
        asUser().with(projectEntity, ProjectEdit.class).execute(() ->
                propertyService.editProperty(
                        projectEntity,
                        propertyTypeClass,
                        data
                )
        );
    }

    protected <T> T getProperty(ProjectEntity projectEntity, Class<? extends PropertyType<T>> propertyTypeClass) throws Exception {
        return asUser().with(projectEntity, ProjectEdit.class).call(() ->
                propertyService.getProperty(
                        projectEntity,
                        propertyTypeClass
                ).getValue()
        );
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
        return doCreateBuild(branch, nameDescription, Signature.of("test"));
    }

    protected Build doCreateBuild(Branch branch, NameDescription nameDescription, Signature signature) throws Exception {
        return asUser().with(branch.projectId(), BuildCreate.class).call(() -> structureService.newBuild(
                Build.of(
                        branch,
                        nameDescription,
                        signature
                )
        ));
    }

    public ValidationRun doValidateBuild(Build build, ValidationStamp vs, ValidationRunStatusID statusId) throws Exception {
        return doValidateBuild(
                build, vs, statusId, null
        );
    }

    public ValidationRun doValidateBuild(
            Build build,
            ValidationStamp vs,
            ValidationRunStatusID statusId,
            ValidationRunData<?> runData
    ) throws Exception {
        return asUser().withView(build).with(build, ValidationRunCreate.class).call(() ->
                structureService.newValidationRun(
                        build,
                        new ValidationRunRequest(
                                vs.getName(),
                                statusId,
                                runData != null ? runData.getDescriptor().getId() : null,
                                runData != null ? runData.getData() : null,
                                null
                        )
                )
        );
    }

    public ValidationRun doValidateBuild(Build build, String vsName, ValidationRunStatusID statusId) throws Exception {
        ValidationStamp vs = doCreateValidationStamp(build.getBranch(), NameDescription.nd(vsName, ""));
        return doValidateBuild(build, vs, statusId);
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

    protected ValidationStamp doCreateValidationStamp(ValidationDataTypeConfig<?> config) throws Exception {
        return doCreateValidationStamp(doCreateBranch(), nameDescription(), config);
    }

    public ValidationStamp doCreateValidationStamp(Branch branch, NameDescription nameDescription) throws Exception {
        return doCreateValidationStamp(branch, nameDescription, null);
    }

    public ValidationStamp doCreateValidationStamp(Branch branch, NameDescription nameDescription, ValidationDataTypeConfig<?> config) throws Exception {
        return asUser().with(branch.getProject().id(), ValidationStampCreate.class).call(() ->
                structureService.newValidationStamp(
                        ValidationStamp.of(
                                branch,
                                nameDescription
                        ).withDataType(config)
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

    protected <T> void doSetProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyType, T data) throws Exception {
        asUser().with(entity, ProjectEdit.class).call(() ->
                propertyService.editProperty(
                        entity,
                        propertyType,
                        data
                )
        );
    }

    protected UserCall asUser() {
        return new UserCall();
    }

    protected AdminCall asAdmin() {
        return new AdminCall();
    }

    protected AnonymousCall asAnonymous() {
        return new AnonymousCall();
    }

    protected UserCall asUserWithView(ProjectEntity... entities) {
        UserCall user = asUser();
        for (ProjectEntity entity : entities) {
            user = user.withView(entity);
        }
        return user;
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

    public void grantViewToAll(boolean grantViewToAll) {
        try {
            asUser().with(GlobalSettings.class).execute(() ->
                    settingsManagerService.saveSettings(
                            SecuritySettings.of().withGrantProjectViewToAll(grantViewToAll)
                    )
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot set GrantViewToAll settings", ex);
        }
    }

    protected <T> T withGrantViewToAll(Callable<T> task) throws Exception {
        grantViewToAll(true);
        return task.call();
    }

    protected <T> T withNoGrantViewToAll(Callable<T> task) throws Exception {
        grantViewToAll(false);
        try {
            return task.call();
        } finally {
            grantViewToAll(false);
        }
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

    protected static class AnonymousCall extends AbstractContextCall {

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            context.setAuthentication(null);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class AccountCall<T extends AccountCall<T>> extends AbstractContextCall {

        protected final Account account;

        public AccountCall(Account account) {
            this.account = account;
        }

        public AccountCall(String name, SecurityRole role) {
            this(Account.of(name, name, name + "@test.com", role, AuthenticationSource.none()));
        }

        @SafeVarargs
        public final T with(Class<? extends GlobalFunction>... fn) {
            account.withGlobalRole(
                    Optional.of(
                            new GlobalRole(
                                    "test", "Test global role", "",
                                    ImmutableSet.copyOf(fn),
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

    protected static class AdminCall extends AccountCall<AdminCall> {

        public AdminCall() {
            super("admin", SecurityRole.ADMINISTRATOR);
        }

    }
}
