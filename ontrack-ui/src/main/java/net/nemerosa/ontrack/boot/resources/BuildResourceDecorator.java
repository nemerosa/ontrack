package net.nemerosa.ontrack.boot.resources;

import com.google.common.collect.Iterables;
import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class BuildResourceDecorator extends AbstractLinkResourceDecorator<Build> {

    private final ResourceDecorationContributorService resourceDecorationContributorService;

    @Autowired
    public BuildResourceDecorator(ResourceDecorationContributorService resourceDecorationContributorService) {
        super(Build.class);
        this.resourceDecorationContributorService = resourceDecorationContributorService;
    }

    @Override
    protected Iterable<LinkDefinition<Build>> getLinkDefinitions() {
        return Iterables.concat(
                Arrays.asList(
                        link(
                                Link.SELF,
                                build -> on(BuildController.class).getBuild(build.getId())
                        ),
                        // Other linked resources
                        link(
                                "_lastPromotionRuns",
                                build -> on(PromotionRunController.class).getLastPromotionRuns(build.getId())
                        ),
                        link(
                                "_promotionRuns",
                                build -> on(PromotionRunController.class).getPromotionRuns(build.getId())
                        ),
                        link(
                                "_validationRuns",
                                build -> on(ValidationRunController.class).getValidationRuns(build.getId())
                        ),
                        link(
                                "_validationStampRunViews",
                                build -> on(ValidationRunController.class).getValidationStampRunViews(build.getId())
                        ),
                        // Creation of a promoted run
                        link(
                                "_promote",
                                build -> on(PromotionRunController.class).newPromotionRunForm(build.getId()),
                                withProjectFn(PromotionRunCreate.class)
                        ),
                        // Creation of a validation run
                        link(
                                "_validate",
                                build -> on(ValidationRunController.class).newValidationRunForm(build.getId()),
                                withProjectFn(ValidationRunCreate.class)
                        ),
                        // Actual properties for this build
                        link(
                                "_properties",
                                build -> on(PropertyController.class).getProperties(ProjectEntityType.BUILD, build.getId())
                        ),
                        // Actions
                        link(
                                "_actions",
                                build -> on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.BUILD, build.getId())
                        ),
                        // Extra information
                        link(
                                "_extra",
                                build -> on(ProjectEntityExtensionController.class).getInformation(ProjectEntityType.BUILD, build.getId())
                        ),
                        // Update link
                        link(
                                Link.UPDATE,
                                build -> on(BuildController.class).updateBuild(build.getId(), null),
                                withProjectFn(BuildEdit.class)
                        ),
                        // Delete link
                        link(
                                Link.DELETE,
                                build -> on(BuildController.class).deleteBuild(build.getId()),
                                withProjectFn(BuildDelete.class)
                        ),
                        // Decorations
                        link(
                                "_decorations",
                                build -> on(DecorationsController.class).getDecorations(build.getProjectEntityType(), build.getId())
                        ),
                        // Events
                        link(
                                "_events",
                                build -> on(EventController.class).getEvents(build.getProjectEntityType(), build.getId(), 0, 10)
                        ),
                        // Signature change
                        link(
                                "_signature",
                                build -> on(BuildController.class).updateBuildSignatureForm(build.getId()),
                                withProjectFn(ProjectEdit.class)
                        ),
                        // Previous & next build
                        link(
                                "_previous",
                                build -> on(BuildController.class).getPreviousBuild(build.getId())
                        ),
                        link(
                                "_next",
                                build -> on(BuildController.class).getNextBuild(build.getId())
                        ),
                        // Build links
                        link(
                                "_buildLinksFrom",
                                build -> on(BuildController.class).getBuildLinksFrom(build.getId())
                        ),
                        link(
                                "_buildLinksTo",
                                build -> on(BuildController.class).getBuildLinksTo(build.getId())
                        ),
                        link(
                                "_buildLinks",
                                build -> on(BuildController.class).getBuildLinkForm(build.getId()),
                                withProjectFn(BuildConfig.class)
                        ),
                        // Page
                        page()
                ),
                // Contributions
                resourceDecorationContributorService.getLinkDefinitions(Build.class)
        );
    }

}
