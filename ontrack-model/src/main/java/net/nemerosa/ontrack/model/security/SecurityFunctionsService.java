package net.nemerosa.ontrack.model.security;

import java.util.Arrays;
import java.util.List;

/**
 * Collection of functions.
 */
public interface SecurityFunctionsService {

    /**
     * List of core global functions.
     */
    List<Class<? extends GlobalFunction>> defaultGlobalFunctions = Arrays.asList(
            GlobalSettings.class,
            ProjectList.class,
            ProjectCreation.class
    );

    /**
     * List of core project functions.
     */
    List<Class<? extends ProjectFunction>> defaultProjectFunctions = Arrays.asList(
            ProjectView.class,
            ProjectEdit.class,
            ProjectConfig.class,
            BranchCreate.class,
            PromotionLevelCreate.class,
            PromotionLevelEdit.class,
            ValidationStampCreate.class,
            ValidationStampEdit.class,
            BuildCreate.class,
            ValidationRunStatusChange.class
    );

    // TODO List of global functions (can be extended)
    // TODO List of project functions (can be extended)

}
