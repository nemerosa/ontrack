package net.nemerosa.ontrack.extension.test;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class TestFeature extends AbstractExtensionFeature {

    public TestFeature() {
        super(
                "test",
                "Test",
                "Test extension",
                ExtensionFeatureOptions.DEFAULT.withGui(true)
        );
    }
}
