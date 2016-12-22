package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class GitHubProjectDecorator extends AbstractExtension implements DecorationExtension<String> {

    private final PropertyService propertyService;

    @Autowired
    public GitHubProjectDecorator(GitHubExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        Property<GitHubProjectConfigurationProperty> property = propertyService.getProperty(entity, GitHubProjectConfigurationPropertyType.class);
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
