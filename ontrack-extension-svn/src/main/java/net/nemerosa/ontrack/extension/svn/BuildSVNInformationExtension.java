package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.EntityInformationExtension;
import net.nemerosa.ontrack.extension.api.model.EntityInformation;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.service.SVNChangeLogService;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This {@link net.nemerosa.ontrack.extension.api.EntityInformationExtension extension} adds some information
 * about the SVN source for a build.
 */
@Component
public class BuildSVNInformationExtension extends AbstractExtension implements EntityInformationExtension {

    private final PropertyService propertyService;
    private final SVNService svnService;
    private final SVNChangeLogService svnChangeLogService;
    private final TransactionService transactionService;

    @Autowired
    public BuildSVNInformationExtension(
            SVNExtensionFeature extensionFeature,
            PropertyService propertyService,
            SVNService svnService,
            SVNChangeLogService svnChangeLogService,
            TransactionService transactionService) {
        super(extensionFeature);
        this.propertyService = propertyService;
        this.svnService = svnService;
        this.svnChangeLogService = svnChangeLogService;
        this.transactionService = transactionService;
    }

    @Override
    public Optional<EntityInformation> getInformation(ProjectEntity entity) {
        if (entity instanceof Build) {
            Build build = (Build) entity;
            // Gets the branch SVN information
            Property<SVNBranchConfigurationProperty> branchConfigurationProperty = propertyService.getProperty(build.getBranch(), SVNBranchConfigurationPropertyType.class);
            Property<SVNProjectConfigurationProperty> projectConfigurationProperty = propertyService.getProperty(build.getBranch().getProject(), SVNProjectConfigurationPropertyType.class);
            if (branchConfigurationProperty.isEmpty() || projectConfigurationProperty.isEmpty()) {
                return Optional.empty();
            } else {
                // Loads the repository
                SVNRepository repository = svnService.getRepository(projectConfigurationProperty.getValue().getConfiguration().getName());
                // Gets the build history
                try (Transaction ignored = transactionService.start()) {
                    return Optional.of(
                            new EntityInformation(
                                    this,
                                    svnChangeLogService.getBuildSVNHistory(repository, build)
                            )
                    );
                }
            }
        } else {
            return Optional.empty();
        }
    }
}
