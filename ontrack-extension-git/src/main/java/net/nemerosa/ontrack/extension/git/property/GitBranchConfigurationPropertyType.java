package net.nemerosa.ontrack.extension.git.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.IndexableBuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.service.BuildGitCommitLinkService;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GitBranchConfigurationPropertyType extends AbstractPropertyType<GitBranchConfigurationProperty> {

    private final BuildGitCommitLinkService buildGitCommitLinkService;
    private final GitService gitService;

    @Autowired
    public GitBranchConfigurationPropertyType(GitExtensionFeature extensionFeature, BuildGitCommitLinkService buildGitCommitLinkService, GitService gitService) {
        super(extensionFeature);
        this.buildGitCommitLinkService = buildGitCommitLinkService;
        this.gitService = gitService;
    }

    @Override
    public String getName() {
        return "Git branch";
    }

    @Override
    public String getDescription() {
        return "Git branch";
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
    public Form getEditionForm(ProjectEntity entity, GitBranchConfigurationProperty value) {
        return Form.create()
                .with(
                        Text.of("branch")
                                .label("Git branch")
                                .value(value != null ? value.getBranch() : "master")
                )
                .with(
                        ServiceConfigurator.of("buildCommitLink")
                                .label("Build commit link")
                                .help("Link between the builds and the Git commits.")
                                .sources(
                                        buildGitCommitLinkService.getLinks().stream()
                                                .map(
                                                        link -> new ServiceConfigurationSource(
                                                                link.getId(),
                                                                link.getName(),
                                                                link.getForm(),
                                                                Collections.singletonMap(
                                                                        "indexationAvailable",
                                                                        link instanceof IndexableBuildGitCommitLink
                                                                )
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .value(
                                        value != null ?
                                                value.getBuildCommitLink() :
                                                null
                                )
                )
                .with(
                        YesNo.of("override")
                                .label("Override builds")
                                .help("Can the existing builds be overridden by a synchronisation? If yes, " +
                                        "the existing validation and promotion runs would be lost as well.")
                                .value(value != null && value.isOverride())
                                .visibleIf("buildCommitLink.extra.indexationAvailable")
                )
                .with(
                        Int.of("buildTagInterval")
                                .label("Build/tag sync. interval (min)")
                                .min(0)
                                .max(60 * 24 * 7) // 1 week
                                .help("Interval in minutes for the synchronisation between builds and tags. " +
                                        "If 0, the synchronisation must be done manually")
                                .value(value != null ? value.getBuildTagInterval() : 0)
                                .visibleIf("buildCommitLink.extra.indexationAvailable")
                );
    }

    @Override
    public GitBranchConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public GitBranchConfigurationProperty fromStorage(JsonNode node) {
        ConfiguredBuildGitCommitLink<?> configuredBuildGitCommitLink;
        if (node.has("buildCommitLink")) {
            JsonNode linkNode = node.get("buildCommitLink");
            configuredBuildGitCommitLink = parseBuildCommitLink(linkNode);
        } else {
            configuredBuildGitCommitLink = null;
        }
        boolean indexationAvailable = configuredBuildGitCommitLink != null && configuredBuildGitCommitLink.getLink() instanceof IndexableBuildGitCommitLink;
        return new GitBranchConfigurationProperty(
                JsonUtils.get(node, "branch", "master"),
                configuredBuildGitCommitLink != null ? configuredBuildGitCommitLink.toServiceConfiguration() : null,
                indexationAvailable && JsonUtils.getBoolean(node, "override", false),
                indexationAvailable ? JsonUtils.getInt(node, "buildTagInterval", 0) : 0
        );
    }

    private <T> ConfiguredBuildGitCommitLink<T> parseBuildCommitLink(JsonNode linkNode) {
        String linkId = JsonUtils.get(linkNode, "id");
        // Gets the link data
        JsonNode linkDataNode = linkNode.get("data");
        // Gets the link
        @SuppressWarnings("unchecked")
        BuildGitCommitLink<T> link = (BuildGitCommitLink<T>) buildGitCommitLinkService.getLink(linkId);
        // Parses the data (for validation)
        T linkData = link.parseData(linkDataNode);
        // OK
        return new ConfiguredBuildGitCommitLink<>(
                link,
                linkData
        );
    }

    @Override
    public String getSearchKey(GitBranchConfigurationProperty value) {
        return value.getBranch();
    }

    @Override
    public GitBranchConfigurationProperty replaceValue(GitBranchConfigurationProperty value, Function<String, String> replacementFunction) {
        return new GitBranchConfigurationProperty(
                replacementFunction.apply(value.getBranch()),
                value.getBuildCommitLink() != null ? replaceBuildCommitLink(value.getBuildCommitLink(), replacementFunction) : null,
                value.isOverride(),
                value.getBuildTagInterval()
        );
    }

    private <T> ServiceConfiguration replaceBuildCommitLink(ServiceConfiguration configuration, Function<String, String> replacementFunction) {
        String linkId = configuration.getId();
        @SuppressWarnings("unchecked")
        BuildGitCommitLink<T> link = (BuildGitCommitLink<T>) buildGitCommitLinkService.getLink(linkId);
        T linkData = link.parseData(configuration.getData());
        T clonedData = link.clone(linkData, replacementFunction::apply);
        JsonNode node = link.toJson(clonedData);
        return new ServiceConfiguration(
                linkId,
                node
        );
    }

    @Override
    public void onPropertyChanged(ProjectEntity entity, GitBranchConfigurationProperty value) {
        gitService.scheduleGitBuildSync((Branch) entity, value);
    }

    @Override
    public void onPropertyDeleted(ProjectEntity entity, GitBranchConfigurationProperty oldValue) {
        gitService.unscheduleGitBuildSync((Branch) entity, oldValue);
    }
}
