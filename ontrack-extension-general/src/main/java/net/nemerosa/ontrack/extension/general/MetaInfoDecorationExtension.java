package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MetaInfoDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final PropertyService propertyService;

    @Autowired
    public MetaInfoDecorationExtension(GeneralExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.allOf(ProjectEntityType.class);
    }

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        // Gets the `metaInfo` property
        return propertyService.getProperty(entity, MetaInfoPropertyType.class)
                .option()
                .map(this::getDecorations)
                .orElse(Collections.<Decoration>emptyList());
    }

    protected List<Decoration> getDecorations(MetaInfoProperty metaInfoProperty) {
        return metaInfoProperty.getItems().stream()
                .map(this::getDecoration)
                .collect(Collectors.toList());
    }

    protected Decoration getDecoration(MetaInfoPropertyItem item) {
        Decoration decoration = Decoration.of(
                this,
                "any",
                ""
        ).withName(
                String.format(
                        "%s: %s",
                        item.getName(),
                        item.getValue()
                )
        );
        String link = item.getLink();
        if (StringUtils.isNotBlank(link)) {
            decoration = decoration.withUri(
                    URI.create(link)
            );
        }
        return decoration;
    }

}
