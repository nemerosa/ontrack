package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class CombinedIssueServiceExtensionFeature extends AbstractExtensionFeature {

    public CombinedIssueServiceExtensionFeature() {
        super("combined", "Combined issue service", "This issue service allows to combine several issue services together",
                ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
