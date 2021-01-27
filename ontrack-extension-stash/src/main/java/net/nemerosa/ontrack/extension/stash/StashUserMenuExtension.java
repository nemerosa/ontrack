package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.support.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Management of Stash configurations available in the user menu.
 */
@Component
public class StashUserMenuExtension extends AbstractExtension implements UserMenuExtension {

    @Autowired
    public StashUserMenuExtension(StashExtensionFeature feature) {
        super(feature);
    }

    @Override
    public Class<? extends GlobalFunction> getGlobalFunction() {
        return GlobalSettings.class;
    }

    @Override
    public Action getAction() {
        return Action.of("stash-configurations", "Bitbucket configurations", "configurations");
    }
}
