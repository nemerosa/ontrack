package net.nemerosa.ontrack.model.security;

import java.util.Arrays;
import java.util.List;

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
            ProjectList.class
    );

    /**
     * List of core project functions.
     */
    List<Class<? extends ProjectFunction>> defaultProjectFunctions = Arrays.asList(
            ProjectView.class,
            ProjectEdit.class,
            ProjectConfig.class,
            ProjectDelete.class,
            BranchCreate.class,
            BranchEdit.class,
            BranchDelete.class,
            PromotionLevelCreate.class,
            PromotionLevelEdit.class,
            PromotionLevelDelete.class,
            ValidationStampCreate.class,
            ValidationStampEdit.class,
            ValidationStampDelete.class,
            BuildCreate.class,
            BuildEdit.class,
            BuildDelete.class,
            ValidationRunCreate.class,
            ValidationRunStatusChange.class,
            PromotionRunCreate.class
    );

    // TODO List of global functions (can be extended)
    // TODO List of project functions (can be extended)

}
