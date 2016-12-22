package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class SVNProjectDecorator extends AbstractExtension implements DecorationExtension<String> {

    private final PropertyService propertyService;

    @Autowired
    public SVNProjectDecorator(SVNExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        Property<SVNProjectConfigurationProperty> property = propertyService.getProperty(entity, SVNProjectConfigurationPropertyType.class);
        if (!property.isEmpty()) {
            return Collections.singletonList(
                    Decoration.of(
                            this,
                            property.getValue().getUrl()
                    )
            );
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }
}
