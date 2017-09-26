package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.support.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Management of the roles and functions.
 */
@Service
@Transactional
public class RolesServiceImpl implements RolesService, StartupService {

    /**
     * Index of global roles
     */
    private final Map<String, GlobalRole> globalRoles = new LinkedHashMap<>();

    /**
     * Index of project roles
     */
    private final Map<String, ProjectRole> projectRoles = new LinkedHashMap<>();

    /**
     * Role contributors
     */
    private final List<RoleContributor> roleContributors;

    @Autowired
    public RolesServiceImpl(List<RoleContributor> roleContributors) {
        this.roleContributors = roleContributors;
    }

    @Override
    public List<GlobalRole> getGlobalRoles() {
        return new ArrayList<>(globalRoles.values());
    }

    @Override
    public Optional<GlobalRole> getGlobalRole(String id) {
        return Optional.ofNullable(globalRoles.get(id));
    }

    @Override
    public List<ProjectRole> getProjectRoles() {
        return new ArrayList<>(projectRoles.values());
    }

    @Override
    public Optional<ProjectRole> getProjectRole(String id) {
        return Optional.ofNullable(projectRoles.get(id));
    }

    @Override
    public List<Class<? extends GlobalFunction>> getGlobalFunctions() {
        return defaultGlobalFunctions;
    }

    @Override
    public List<Class<? extends ProjectFunction>> getProjectFunctions() {
        return defaultProjectFunctions;
    }

    @Override
    public Optional<ProjectRoleAssociation> getProjectRoleAssociation(int project, String roleId) {
        return getProjectRole(roleId).map(role -> new ProjectRoleAssociation(project, role));
    }

    @Override
    public String getName() {
        return "Roles";
    }

    @Override
    public int startupOrder() {
        return 50;
    }

    @Override
    public void start() {
        // Global roles
        initGlobalRoles();
        // Project roles
        initProjectRoles();
    }

    private void initProjectRoles() {

        // Owner
        register(Roles.PROJECT_OWNER, "Project owner",
                "The project owner is allowed to all functions in a project, but for its deletion.",
                getProjectFunctions().stream()
                        .filter(t -> !ProjectDelete.class.isAssignableFrom(t))
                        .collect(Collectors.toList())
        );

        // Participant
        register(Roles.PROJECT_PARTICIPANT, "Participant",
                "A participant in a project is allowed to change statuses in validation runs.",
                Arrays.asList(
                        ProjectView.class,
                        ValidationRunStatusChange.class
                )
        );

        // Validation manager
        List<Class<? extends ProjectFunction>> validationManagerFunctions = Arrays.asList(
                ValidationStampCreate.class,
                ValidationStampEdit.class,
                ValidationStampDelete.class,
                ValidationRunCreate.class,
                ValidationRunStatusChange.class
        );
        register(Roles.PROJECT_VALIDATION_MANAGER, "Validation manager",
                "The validation manager can manage the validation stamps.",
                validationManagerFunctions
        );

        // Promoter
        List<Class<? extends ProjectFunction>> promoterFunctions = Arrays.asList(
                PromotionRunCreate.class,
                PromotionRunDelete.class,
                ValidationRunStatusChange.class
        );
        register(Roles.PROJECT_PROMOTER, "Promoter",
                "The promoter can promote existing builds.",
                promoterFunctions
        );

        // Project manager
        List<Class<? extends ProjectFunction>> projectManagerFunctions = new ArrayList<>();
        projectManagerFunctions.addAll(validationManagerFunctions);
        projectManagerFunctions.addAll(promoterFunctions);
        projectManagerFunctions.add(BranchFilterMgt.class);
        projectManagerFunctions.add(BranchCreate.class);
        projectManagerFunctions.add(BranchEdit.class);
        projectManagerFunctions.add(BranchDelete.class);
        register(Roles.PROJECT_MANAGER, "Project manager",
                "The project manager can promote existing builds, manage the validation stamps, " +
                        "manage the shared build filters, manage the branches and edit some properties.",
                projectManagerFunctions
        );

        // Read only on a project
        register(Roles.PROJECT_READ_ONLY, "Read Only",
                "This role grants a read-only access to all components of the projects",
                readOnlyProjectFunctions
        );

        // Project roles contributions
        roleContributors.forEach(roleContributor ->
                roleContributor.getProjectRoles().forEach(roleDefinition -> {
                            if (Roles.PROJECT_ROLES.contains(roleDefinition.getId())) {
                                // Totally illegal - stopping everything
                                throw new IllegalStateException("An existing project role cannot be overridden: " + roleDefinition.getId());
                            } else {
                                register(
                                        roleDefinition.getId(),
                                        roleDefinition.getName(),
                                        roleDefinition.getDescription(),
                                        Collections.emptyList()
                                );
                            }
                        }
                )
        );

    }

    private void register(String id, String name, String description, List<Class<? extends ProjectFunction>> projectFunctions) {
        LinkedHashSet<Class<? extends ProjectFunction>> functions = new LinkedHashSet<>(projectFunctions);
        // Contributions
        roleContributors.forEach(roleContributor ->
                roleContributor.getProjectFunctionContributionsForProjectRole(id).forEach(fn -> {
                    // Checks if the role is predefined
                    if (Roles.PROJECT_ROLES.contains(id)) {
                        // Checks the function as non core
                        checkFunctionForContribution(fn);
                    }
                    // OK
                    functions.add(fn);
                })
        );
        // OK
        register(new ProjectRole(
                id,
                name,
                description,
                functions
        ));
    }

    private void checkFunctionForContribution(Class<?> fn) {
        CoreFunction coreFunction = fn.getDeclaredAnnotation(CoreFunction.class);
        if (coreFunction != null) {
            // Totally illegal - stopping everything
            throw new IllegalStateException("A core function cannot be added to an existing role.");
        }
    }

    private void register(ProjectRole projectRole) {
        projectRoles.put(projectRole.getId(), projectRole);

    }

    private void initGlobalRoles() {

        // Administrator
        register(Roles.GLOBAL_ADMINISTRATOR, "Administrator",
                "An administrator is allowed to do everything in the application.",
                getGlobalFunctions(),
                getProjectFunctions());

        // Creator
        register(Roles.GLOBAL_CREATOR, "Creator",
                "A creator is allowed to create new projects and to configure it. Once done, its rights on the " +
                        "project are revoked immediately.",
                Collections.singletonList(
                        ProjectCreation.class
                ),
                Arrays.asList(
                        // Structure creation functions only
                        ProjectConfig.class,
                        BranchCreate.class,
                        BranchTemplateMgt.class,
                        PromotionLevelCreate.class,
                        ValidationStampCreate.class
                )
        );

        // Creator
        register(Roles.GLOBAL_AUTOMATION, "Automation",
                "This role can be assigned to users or groups which must automate Ontrack. It aggregates both the " +
                        "Creator and the Controller roles into one.",
                Arrays.asList(
                        ProjectCreation.class,
                        AccountGroupManagement.class
                ),
                Arrays.asList(
                        // Structure creation functions only
                        ProjectConfig.class,
                        ProjectAuthorisationMgt.class,
                        BranchCreate.class,
                        BranchTemplateMgt.class,
                        PromotionLevelCreate.class,
                        PromotionLevelEdit.class,
                        ValidationStampCreate.class,
                        ValidationStampEdit.class,
                        ProjectView.class,
                        BuildCreate.class,
                        BuildConfig.class,
                        PromotionRunCreate.class,
                        ValidationRunCreate.class,
                        BranchTemplateSync.class
                )
        );

        // Controller
        register(Roles.GLOBAL_CONTROLLER, "Controller",
                "A controller, is allowed to create builds, promotion runs and validation runs. He can also " +
                        "synchronise templates. This role is typically granted to continuous integration tools.",
                Collections.emptyList(),
                Arrays.asList(
                        ProjectView.class,
                        BuildCreate.class,
                        BuildConfig.class,
                        PromotionRunCreate.class,
                        ValidationRunCreate.class,
                        BranchTemplateSync.class
                )
        );

        // Read only on all projects
        register(Roles.GLOBAL_READ_ONLY, "Read Only",
                "This role grants a read-only access to all projects",
                readOnlyGlobalFunctions,
                readOnlyProjectFunctions
        );

        // Global roles contributions
        roleContributors.forEach(roleContributor ->
                roleContributor.getGlobalRoles().forEach(roleDefinition -> {
                            if (Roles.GLOBAL_ROLES.contains(roleDefinition.getId())) {
                                // Totally illegal - stopping everything
                                throw new IllegalStateException("An existing global role cannot be overridden: " + roleDefinition.getId());
                            } else {
                                register(
                                        roleDefinition.getId(),
                                        roleDefinition.getName(),
                                        roleDefinition.getDescription(),
                                        Collections.emptyList(),
                                        Collections.emptyList()
                                );
                            }
                        }
                )
        );

    }

    private void register(String id, String name, String description, List<Class<? extends GlobalFunction>> globalFunctions, List<Class<? extends ProjectFunction>> projectFunctions) {
        // Global functions and contributions
        LinkedHashSet<Class<? extends GlobalFunction>> gfns = new LinkedHashSet<>(globalFunctions);
        roleContributors.forEach(roleContributor ->
                roleContributor.getGlobalFunctionContributionsForGlobalRole(id).forEach(fn -> {
                    if (Roles.GLOBAL_ROLES.contains(id)) {
                        checkFunctionForContribution(fn);
                    }
                    gfns.add(fn);
                })
        );
        // Project functions
        LinkedHashSet<Class<? extends ProjectFunction>> pfns = new LinkedHashSet<>(projectFunctions);
        roleContributors.forEach(roleContributor ->
                roleContributor.getProjectFunctionContributionsForGlobalRole(id).forEach(fn -> {
                    if (Roles.GLOBAL_ROLES.contains(id)) {
                        checkFunctionForContribution(fn);
                    }
                    pfns.add(fn);
                })
        );
        // OK
        register(new GlobalRole(
                id,
                name,
                description,
                gfns,
                pfns
        ));
    }

    private void register(GlobalRole role) {
        globalRoles.put(role.getId(), role);
    }
}
