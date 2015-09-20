package net.nemerosa.ontrack.service.support.property;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.PropertyTypeExtension;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestPropertyTypeExtension implements PropertyTypeExtension<TestProperty> {

    private final TestExtensionFeature extensionFeature;
    private final TestPropertyType propertyType;

    @Autowired
    public TestPropertyTypeExtension(TestExtensionFeature extensionFeature) {
        this.extensionFeature = extensionFeature;
        this.propertyType = new TestPropertyType(
                new TestExtensionFeature()
        );
    }


    @Override
    public PropertyType<TestProperty> getPropertyType() {
        return propertyType;
    }

    @Override
    public ExtensionFeature getFeature() {
        return extensionFeature;
    }
}
