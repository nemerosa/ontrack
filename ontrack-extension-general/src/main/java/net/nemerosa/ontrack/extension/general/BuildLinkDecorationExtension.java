package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuildLinkDecorationExtension extends AbstractExtension implements DecorationExtension<BuildLinkDecoration> {

    private final StructureService structureService;
    private final URIBuilder uriBuilder;

    @Autowired
    public BuildLinkDecorationExtension(GeneralExtensionFeature extensionFeature, StructureService structureService, URIBuilder uriBuilder) {
        super(extensionFeature);
        this.structureService = structureService;
        this.uriBuilder = uriBuilder;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public List<Decoration<BuildLinkDecoration>> getDecorations(ProjectEntity entity) {
        return structureService.getBuildLinksFrom((Build) entity).stream()
                .map(this::getDecoration)
                .collect(Collectors.toList());
    }

    protected Decoration<BuildLinkDecoration> getDecoration(Build build) {
        // Gets the list of promotion runs for this build
        List<PromotionRun> promotionRuns = structureService.getLastPromotionRunsForBuild(build.getId());
        // Decoration
        return Decoration.of(this, new BuildLinkDecoration(
                build.getProject().getName(),
                build.getName(),
                uriBuilder.getEntityPage(build),
                promotionRuns
        ));
    }

}
