package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class SVNBranchConfigurationPropertyType extends AbstractPropertyType<SVNBranchConfigurationProperty> {

    private final PropertyService propertyService;

    @Autowired
    public SVNBranchConfigurationPropertyType(SVNExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public String getName() {
        return "SVN configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the branch with Subversion.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

    /**
     * One can edit the SVN configuration of a branch only if he can configurure a project and if the project
     * is itself configured with SVN.
     */
    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class) &&
                propertyService.hasProperty(
                        ((Branch) entity).getProject(),
                        SVNProjectConfigurationPropertyType.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, SVNBranchConfigurationProperty value) {
        return Form.create()
                .with(
                        Text.of("branchPath")
                                .label("Branch path")
                                .help("Path of the branch in the Subversion repository. The path is relative to the root " +
                                        "of the repository.")
                                .value(value != null ? value.getBranchPath() : "/project/branches/xxx")
                )
                .with(
                        Text.of("buildPath")
                                .label("Build path")
                                .help("@file:extension/svn/help.net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType.buildPath.tpl.html")
                                .value(value != null ? value.getBuildPath() : "/project/tags/{build}")
                );
    }

    @Override
    public JsonNode forStorage(SVNBranchConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("branchPath", value.getBranchPath())
                        .with("buildPath", value.getBuildPath())
                        .get()
        );
    }

    @Override
    public SVNBranchConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public SVNBranchConfigurationProperty fromStorage(JsonNode node) {
        String branchPath = node.path("branchPath").asText();
        String buildPath = node.path("buildPath").asText();
        // Validates the paths
        validateNotBlank(branchPath, "The branch path must not be empty");
        validateNotBlank(buildPath, "The build path must not be empty");
        // OK
        return new SVNBranchConfigurationProperty(
                branchPath,
                buildPath
        );
    }

    @Override
    public String getSearchKey(SVNBranchConfigurationProperty value) {
        return value.getBranchPath();
    }

    @Override
    public SVNBranchConfigurationProperty replaceValue(SVNBranchConfigurationProperty value, Function<String, String> replacementFunction) {
        return new SVNBranchConfigurationProperty(
                replacementFunction.apply(value.getBranchPath()),
                replacementFunction.apply(value.getBuildPath())
        );
    }
}
