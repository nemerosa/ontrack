package net.nemerosa.ontrack.extension.api.support;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * {@link net.nemerosa.ontrack.model.structure.Decorator} which can be used for tests.
 */
@Component
public class TestDecorator extends AbstractExtension implements DecorationExtension<TestDecorationData> {

    private final PropertyService propertyService;

    @Autowired
    public TestDecorator(TestExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<TestDecorationData>> getDecorations(ProjectEntity entity) {
        return propertyService.getProperty(entity, TestDecoratorPropertyType.class)
                .option()
                .map(data -> Collections.singletonList(
                        Decoration.of(
                                TestDecorator.this,
                                data
                        )
                ))
                .orElse(Collections.emptyList());
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.allOf(ProjectEntityType.class);
    }
}
