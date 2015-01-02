package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.BuildValidationExtension;
import net.nemerosa.ontrack.extension.api.model.BuildValidationException;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNBuildValidationExtension extends AbstractExtension implements BuildValidationExtension {

    private final PropertyService propertyService;

    @Autowired
    public SVNBuildValidationExtension(SVNExtensionFeature feature, PropertyService propertyService) {
        super(feature);
        this.propertyService = propertyService;
    }

    @Override
    public void validateBuild(Build build) throws BuildValidationException {
        propertyService.getProperty(build.getBranch(), SVNBranchConfigurationPropertyType.class).option().ifPresent(property -> {
            String buildPath = property.getBuildPath();
            try {
                SVNUtils.expandBuildPath(buildPath, build);
            } catch (Exception ex) {
                throw new BuildValidationException(
                        String.format(
                                "Build %s does not comply with the SVN configuration: %s",
                                build.getName(),
                                buildPath
                        )
                );
            }
        });
    }
}
