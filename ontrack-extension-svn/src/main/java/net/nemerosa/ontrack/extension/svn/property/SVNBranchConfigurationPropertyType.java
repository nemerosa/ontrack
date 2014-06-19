package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;

public class SVNBranchConfigurationPropertyType extends AbstractPropertyType<SVNBranchConfigurationProperty> {

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

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(SVNBranchConfigurationProperty value) {
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
                                .help("Path of a tag in the Subversion repository using a build name. The path is " +
                                        "computed relative to the root of the repository. Several placeholders can " +
                                        "be defined in the path definition, that will be replaced at runtime: " +
                                        "{build} for the build name.")
                                        // TODO Property placeholder
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
}
