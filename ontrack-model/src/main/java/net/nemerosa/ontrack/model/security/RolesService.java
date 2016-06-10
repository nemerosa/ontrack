package net.nemerosa.ontrack.model.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Management of roles and functions.
 *
 * @see net.nemerosa.ontrack.model.security.GlobalRole
 * @see net.nemerosa.ontrack.model.security.ProjectRole
 * @see net.nemerosa.ontrack.model.security.GlobalFunction
 * @see net.nemerosa.ontrack.model.security.ProjectFunction
 */
public interface RolesService {

    /**
     * List of core global functions.
     */
    List<Class<? extends GlobalFunction>> defaultGlobalFunctions = Arrays.asList(
            ProjectCreation.class,
            ApplicationManagement.class,
            GlobalSettings.class,
            AccountManagement.class,
            ProjectList.class
    );

    /**
     * List of core project functions.
     */
    List<Class<? extends ProjectFunction>> defaultProjectFunctions = Arrays.asList(
            ProjectView.class,
            ProjectEdit.class,
            ProjectAuthorisationMgt.class,
            ProjectConfig.class,
            ProjectDelete.class,
            ProjectAuthorisationsManagement.class,
            BranchCreate.class,
            BranchEdit.class,
            BranchFilterMgt.class,
            BranchTemplateMgt.class,
            BranchDelete.class,
            PromotionLevelCreate.class,
            PromotionLevelEdit.class,
            PromotionLevelDelete.class,
            ValidationStampCreate.class,
            ValidationStampEdit.class,
            ValidationStampDelete.class,
            BuildCreate.class,
            BuildConfig.class,
            BuildEdit.class,
            BuildDelete.class,
            ValidationRunCreate.class,
            ValidationRunStatusChange.class,
            PromotionRunCreate.class,
            PromotionRunDelete.class
    );

    /**
     * List of global roles.
     */
    List<GlobalRole> getGlobalRoles();

    /**
     * Gets a global role by its identifier
     */
    Optional<GlobalRole> getGlobalRole(String id);

    /**
     * List of project roles
     */
    List<ProjectRole> getProjectRoles();

    /**
     * Gets a project role by its identifier
     */
    Optional<ProjectRole> getProjectRole(String id);

    /**
     * List of all global functions
     */
    List<Class<? extends GlobalFunction>> getGlobalFunctions();

    /**
     * List of all project functions
     */
    List<Class<? extends ProjectFunction>> getProjectFunctions();

    /**
     * Gets a project/role association
     *
     * @param project Project ID
     * @param roleId  Role name
     * @return Project/role association or {@linkplain java.util.Optional#empty() empty} if the role
     * does not exist
     */
    Optional<ProjectRoleAssociation> getProjectRoleAssociation(int project, String roleId);
}
