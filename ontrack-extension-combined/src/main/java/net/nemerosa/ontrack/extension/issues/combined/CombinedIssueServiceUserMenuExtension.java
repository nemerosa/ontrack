package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.support.Action;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CombinedIssueServiceUserMenuExtension extends AbstractExtension implements UserMenuExtension {

    @Autowired
    public CombinedIssueServiceUserMenuExtension(CombinedIssueServiceExtensionFeature feature) {
        super(feature);
    }

    @Override
    public Class<? extends GlobalFunction> getGlobalFunction() {
        return GlobalSettings.class;
    }

    @NotNull
    @Override
    public Action getAction() {
        return Action.of("combined-issue-service-configurations", "Combined issue services configurations", "configurations")
                .withGroup(UserMenuExtensionGroups.configuration);
    }
}
