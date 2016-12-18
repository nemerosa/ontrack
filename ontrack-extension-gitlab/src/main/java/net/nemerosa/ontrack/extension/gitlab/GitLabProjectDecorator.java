package net.nemerosa.ontrack.extension.gitlab;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class GitLabProjectDecorator extends AbstractExtension implements DecorationExtension<String> {

    private final PropertyService propertyService;

    @Autowired
    public GitLabProjectDecorator(GitLabExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        Property<GitLabProjectConfigurationProperty> property = propertyService.getProperty(entity, GitLabProjectConfigurationPropertyType.class);
        if (!property.isEmpty()) {
            return Collections.singletonList(
                    Decoration.of(
                            this,
                            String.format(
                                    "%s @ %s",
                                    property.getValue().getRepository(),
                                    property.getValue().getConfiguration().getName()
                            )
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
