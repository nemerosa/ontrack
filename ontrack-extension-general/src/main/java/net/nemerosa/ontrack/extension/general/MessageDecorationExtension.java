package net.nemerosa.ontrack.extension.general;

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

@Component
public class MessageDecorationExtension extends AbstractExtension implements DecorationExtension<MessageProperty> {

    private final PropertyService propertyService;

    @Autowired
    public MessageDecorationExtension(GeneralExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.allOf(ProjectEntityType.class);
    }

    @Override
    public List<Decoration<MessageProperty>> getDecorations(ProjectEntity entity) {
        // Gets the `message` property
        return propertyService.getProperty(entity, MessagePropertyType.class)
                .option()
                .map(
                        messageProperty ->
                                Collections.singletonList(
                                        Decoration.of(
                                                this,
                                                messageProperty
                                        )
                                )
                )
                .orElse(Collections.<Decoration<MessageProperty>>emptyList());
    }

}
