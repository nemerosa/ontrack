package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SVNBranchSyncActionExtension extends AbstractExtension implements ProjectEntityActionExtension {

    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public SVNBranchSyncActionExtension(
            SVNExtensionFeature extensionFeature,
            PropertyService propertyService,
            SecurityService securityService) {
        super(extensionFeature);
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @Override
    public Optional<Action> getAction(ProjectEntity entity) {
        if (entity instanceof Branch
                && propertyService.hasProperty(entity, SVNBranchConfigurationPropertyType.class)
                && securityService.isProjectFunctionGranted(entity, BuildCreate.class)) {
            return Optional.of(Action.of(
                    "svn-sync",
                    "SVN <-> Build sync",
                    String.format("sync/%d", entity.id())
            ));
        } else {
            return Optional.empty();
        }
    }

}
