package net.nemerosa.ontrack.extension.git.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.service.BuildGitCommitLinkService;
import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GitBranchConfigurationPropertyType extends AbstractPropertyType<GitBranchConfigurationProperty> {

    private final BuildGitCommitLinkService buildGitCommitLinkService;

    public GitBranchConfigurationPropertyType(BuildGitCommitLinkService buildGitCommitLinkService) {
        this.buildGitCommitLinkService = buildGitCommitLinkService;
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
    public Form getEditionForm(GitBranchConfigurationProperty value) {
        return Form.create()
                .with(
                        Text.of("branch")
                                .label("Git branch")
                                .value(value != null ? value.getBranch() : "master")
                )
                        // FIXME #163 Remove tag pattern
                .with(
                        Text.of("tagPattern")
                                .label("Tag pattern")
                                .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType.tagPattern.tpl.html")
                                .value(value != null ? value.getTagPattern() : "*")
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
                                                                link.getForm()
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
                )
                .with(
                        Int.of("buildTagInterval")
                                .label("Build/tag sync. interval (min)")
                                .min(0)
                                .max(60 * 24 * 7) // 1 week
                                .help("Interval in minutes for the synchronisation between builds and tags. " +
                                        "If 0, the synchronisation must be done manually")
                                .value(value != null ? value.getBuildTagInterval() : 0)
                );
    }

    @Override
    public GitBranchConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public GitBranchConfigurationProperty fromStorage(JsonNode node) {
        return new GitBranchConfigurationProperty(
                JsonUtils.get(node, "branch", "master"),
                JsonUtils.get(node, "tagPattern", "*"),
                node.has("buildCommitLink") ?
                        ServiceConfiguration.of(node.get("buildCommitLink")) :
                        TagBuildNameGitCommitLink.DEFAULT.toServiceConfiguration(),
                JsonUtils.getBoolean(node, "override", false),
                JsonUtils.getInt(node, "buildTagInterval", 0)
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
                replacementFunction.apply(value.getTagPattern()),
                replaceBuildCommitLink(value.getBuildCommitLink(), replacementFunction),
                value.isOverride(),
                value.getBuildTagInterval()
        );
    }

    protected <T> ServiceConfiguration replaceBuildCommitLink(ServiceConfiguration configuration, Function<String, String> replacementFunction) {
        String linkId = configuration.getId();
        @SuppressWarnings("unchecked")
        BuildGitCommitLink<T> link = (BuildGitCommitLink<T>) buildGitCommitLinkService.getLink(linkId);
        T linkData = link.parseData(configuration.getData());
        T clonedData = link.clone(linkData, replacementFunction);
        JsonNode node = link.toJson(clonedData);
        return new ServiceConfiguration(
                linkId,
                node
        );
    }
}
