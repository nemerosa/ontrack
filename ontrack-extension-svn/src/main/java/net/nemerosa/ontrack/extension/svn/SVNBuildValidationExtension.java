package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.BuildValidationExtension;
import net.nemerosa.ontrack.extension.api.model.BuildValidationException;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLinkService;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.support.ConfiguredBuildSvnRevisionLink;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNBuildValidationExtension extends AbstractExtension implements BuildValidationExtension {

    private final PropertyService propertyService;
    private final BuildSvnRevisionLinkService buildSvnRevisionLinkService;

    @Autowired
    public SVNBuildValidationExtension(SVNExtensionFeature feature, PropertyService propertyService, BuildSvnRevisionLinkService buildSvnRevisionLinkService) {
        super(feature);
        this.propertyService = propertyService;
        this.buildSvnRevisionLinkService = buildSvnRevisionLinkService;
    }

    @Override
    public void validateBuild(Build build) throws BuildValidationException {
        propertyService.getProperty(build.getBranch(), SVNBranchConfigurationPropertyType.class).option().ifPresent(property -> {
            // Gets the configured link
            ConfiguredBuildSvnRevisionLink<Object> revisionLink =
                    buildSvnRevisionLinkService.getConfiguredBuildSvnRevisionLink(property.getBuildRevisionLink());
            // Tests the validity of the build name
            if (!revisionLink.isValidBuildName(build.getName())) {
                throw new BuildValidationException(
                        String.format(
                                "Build %s does not comply with the SVN configuration",
                                build.getName()
                        )
                );
            }
        });
    }
}
