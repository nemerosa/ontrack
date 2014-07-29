package net.nemerosa.ontrack.extension.svn.property;

import net.nemerosa.ontrack.extension.scm.property.AbstractSCMChangeLogIssueValidator;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.PropertyService;

public abstract class AbstractSVNChangeLogIssueValidator<T> extends AbstractSCMChangeLogIssueValidator<T> {

    private final PropertyService propertyService;

    public AbstractSVNChangeLogIssueValidator(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        Branch branch = (Branch) entity;
        return securityService.isProjectFunctionGranted(branch, ProjectConfig.class)
                && canApplyTo(branch);
    }

    protected boolean canApplyTo(Branch branch) {
        return propertyService.hasProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class)
                && propertyService.hasProperty(branch, SVNBranchConfigurationPropertyType.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

}
