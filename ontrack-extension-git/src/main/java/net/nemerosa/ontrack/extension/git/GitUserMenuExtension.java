package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitUserMenuExtension extends AbstractExtension implements UserMenuExtension {

    @Autowired
    public GitUserMenuExtension(GitExtensionFeature feature) {
        super(feature);
    }

    @Override
    public Class<? extends GlobalFunction> getGlobalFunction() {
        return GlobalSettings.class;
    }

    @NotNull
    @Override
    public Action getAction() {
        return Action.of("git-configurations", "Git configurations", "configurations")
                .withGroup(UserMenuExtensionGroups.configuration);
    }
}
