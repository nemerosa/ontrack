package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLinkService;
import net.nemerosa.ontrack.extension.svn.support.ConfiguredBuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.support.TagNameSvnRevisionLink;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.ServiceConfigurator;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SVNBranchConfigurationPropertyType extends AbstractPropertyType<SVNBranchConfigurationProperty> {

    private final PropertyService propertyService;
    private final BuildSvnRevisionLinkService buildSvnRevisionLinkService;

    @Autowired
    public SVNBranchConfigurationPropertyType(SVNExtensionFeature extensionFeature, PropertyService propertyService, BuildSvnRevisionLinkService buildSvnRevisionLinkService) {
        super(extensionFeature);
        this.propertyService = propertyService;
        this.buildSvnRevisionLinkService = buildSvnRevisionLinkService;
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
                        entity.getProject(),
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
                        ServiceConfigurator.of("buildRevisionLink")
                                .label("Build revision link")
                                .help("Link between the builds and the Svn revisions.")
                                .sources(
                                        buildSvnRevisionLinkService.getLinks().stream()
                                                .map(
                                                        link -> new ServiceConfigurationSource(
                                                                link.getId(),
                                                                link.getName(),
                                                                link.getForm()
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .value(
                                        value != null ?
                                                value.getBuildRevisionLink() :
                                                null
                                )
                );
    }

    @Override
    public SVNBranchConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public SVNBranchConfigurationProperty fromStorage(JsonNode node) {
        // Branch path
        String branchPath = node.path("branchPath").asText();
        validateNotBlank(branchPath, "The branch path must not be empty");
        // Link
        ConfiguredBuildSvnRevisionLink<?> configuredBuildSvnRevisionLink;
        if (node.has("buildRevisionLink")) {
            JsonNode linkNode = node.get("buildRevisionLink");
            configuredBuildSvnRevisionLink = parseBuildRevisionLink(linkNode);
        } else {
            configuredBuildSvnRevisionLink = TagNameSvnRevisionLink.DEFAULT;
        }
        // OK
        return new SVNBranchConfigurationProperty(
                branchPath,
                configuredBuildSvnRevisionLink.toServiceConfiguration(),
                ""
        );
    }

    private <T> ConfiguredBuildSvnRevisionLink<T> parseBuildRevisionLink(JsonNode linkNode) {
        String linkId = JsonUtils.get(linkNode, "id");
        // Gets the link data
        JsonNode linkDataNode = linkNode.get("data");
        // Gets the configured link
        return buildSvnRevisionLinkService.getConfiguredBuildSvnRevisionLink(
                linkId,
                linkDataNode
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
                replaceBuildRevisionLink(value.getBuildRevisionLink(), replacementFunction),
                ""
        );
    }

    private <T> ServiceConfiguration replaceBuildRevisionLink(ServiceConfiguration configuration, Function<String, String> replacementFunction) {
        String linkId = configuration.getId();
        @SuppressWarnings("unchecked")
        BuildSvnRevisionLink<T> link = (BuildSvnRevisionLink<T>) buildSvnRevisionLinkService.getLink(linkId);
        T linkData = link.parseData(configuration.getData());
        T clonedData = link.clone(linkData, replacementFunction);
        JsonNode node = link.toJson(clonedData);
        return new ServiceConfiguration(
                linkId,
                node
        );
    }
}
