package net.nemerosa.ontrack.extension.jira;

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
public class JIRAUserMenuExtension extends AbstractExtension implements UserMenuExtension {

    @Autowired
    public JIRAUserMenuExtension(JIRAExtensionFeature feature) {
        super(feature);
    }

    @Override
    public Class<? extends GlobalFunction> getGlobalFunction() {
        return GlobalSettings.class;
    }

    @NotNull
    @Override
    public Action getAction() {
        return Action.of("jira-configurations", "JIRA configurations", "configurations")
                .withGroup(UserMenuExtensionGroups.configuration);
    }
}
