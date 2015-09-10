package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BuildLinkDecorationExtension extends AbstractExtension implements DecorationExtension<BuildLinkDecoration> {

    private final StructureService structureService;
    private final URIBuilder uriBuilder;
    private final PropertyService propertyService;

    @Autowired
    public BuildLinkDecorationExtension(GeneralExtensionFeature extensionFeature, StructureService structureService, URIBuilder uriBuilder, PropertyService propertyService) {
        super(extensionFeature);
        this.structureService = structureService;
        this.uriBuilder = uriBuilder;
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public List<Decoration<BuildLinkDecoration>> getDecorations(ProjectEntity entity) {
        // Gets the `buildLink` property
        return propertyService.getProperty(entity, BuildLinkPropertyType.class)
                .option()
                .map(this::getDecorations)
                .orElse(Collections.<Decoration<BuildLinkDecoration>>emptyList());
    }

    protected List<Decoration<BuildLinkDecoration>> getDecorations(BuildLinkProperty buildLinkProperty) {
        return buildLinkProperty.getLinks().stream()
                .map(this::getDecoration)
                .collect(Collectors.toList());
    }

    protected Decoration<BuildLinkDecoration> getDecoration(BuildLinkPropertyItem item) {
        Optional<Build> oBuild = item.findBuild(structureService);
        if (oBuild.isPresent()) {
            Build build = oBuild.get();
            // Gets the list of promotion runs for this build
            List<PromotionRun> promotionRuns = structureService.getLastPromotionRunsForBuild(build.getId());
            // Decoration
            return Decoration.of(this, new BuildLinkDecoration(
                    item.getProject(),
                    item.getBuild(),
                    uriBuilder.getEntityPage(build),
                    promotionRuns
            ));
        } else {
            return Decoration.of(this, BuildLinkDecoration.found(
                    item.getProject(),
                    item.getBuild()));
        }
    }

}
