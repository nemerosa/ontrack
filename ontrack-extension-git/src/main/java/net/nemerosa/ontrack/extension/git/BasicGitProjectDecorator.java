package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class BasicGitProjectDecorator extends AbstractExtension implements DecorationExtension<String> {

    private final PropertyService propertyService;

    @Autowired
    public BasicGitProjectDecorator(GitExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        Property<GitProjectConfigurationProperty> property = propertyService.getProperty(entity, GitProjectConfigurationPropertyType.class);
        if (!property.isEmpty()) {
            return Collections.singletonList(
                    Decoration.of(
                            this,
                            property.getValue().getConfiguration().getRemote()
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
