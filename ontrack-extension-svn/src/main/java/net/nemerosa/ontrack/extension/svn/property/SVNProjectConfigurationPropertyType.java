package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;

public class SVNProjectConfigurationPropertyType extends AbstractPropertyType<SVNProjectConfigurationProperty> {

    @Override
    public String getName() {
        return "Subversion configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a Subversion repository";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(SVNProjectConfigurationProperty value) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType.getEditionForm
        return null;
    }

    @Override
    public SVNProjectConfigurationProperty fromClient(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType.fromClient
        return null;
    }

    @Override
    public SVNProjectConfigurationProperty fromStorage(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType.fromStorage
        return null;
    }

    @Override
    public String getSearchKey(SVNProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }
}
