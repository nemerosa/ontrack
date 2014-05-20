package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import org.springframework.stereotype.Component;

@Component
public class JenkinsUserMenuExtension extends AbstractExtension implements UserMenuExtension {

    public JenkinsUserMenuExtension() {
        super("jenkins", "", "");
    }

    @Override
    public Class<? extends GlobalFunction> getGlobalFunction() {
        return GlobalSettings.class;
    }

}
